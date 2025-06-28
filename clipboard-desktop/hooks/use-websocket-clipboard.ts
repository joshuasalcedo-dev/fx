import { useEffect, useRef, useCallback, useState } from 'react';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { toast } from 'sonner';
import type { ClipboardDto } from '@/lib/api-client';

interface UseWebSocketClipboardProps {
    onNewEntry: (entry: ClipboardDto) => void;
    onUpdateEntry: (entry: ClipboardDto) => void;
    onDeleteEntry: (entryId: number) => void;
    onClear: (includePinned: boolean) => void;
    enabled?: boolean;
}

export function useWebSocketClipboard({
                                          onNewEntry,
                                          onUpdateEntry,
                                          onDeleteEntry,
                                          onClear,
                                          enabled = true
                                      }: UseWebSocketClipboardProps) {
    const clientRef = useRef<Client | null>(null);
    // @ts-ignore
    const reconnectTimeoutRef = useRef<NodeJS.Timeout>();
    const isConnectedRef = useRef(false);

    const [isConnected, setIsConnected] = useState(false);

    const connect = useCallback(() => {
        if (!enabled || clientRef.current?.connected) return;

        const client = new Client({
            brokerURL: 'ws://localhost:5000/ws-clipboard',
            webSocketFactory: () => {
                return new SockJS('http://localhost:5000/ws-clipboard');
            },
            connectHeaders: {},
            debug: (str) => {
                console.log('[STOMP] ' + str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = (frame) => {
            console.log('Connected to WebSocket');
            isConnectedRef.current = true;
            setIsConnected(true);

            // Subscribe to new entries
            client.subscribe('/topic/clipboard/new', (message: IMessage) => {
                try {
                    const entry: ClipboardDto = JSON.parse(message.body);
                    console.log('New clipboard entry received:', entry);
                    onNewEntry(entry);
                } catch (error) {
                    console.error('Error parsing new entry:', error);
                }
            });

            // Subscribe to updates
            client.subscribe('/topic/clipboard/update', (message: IMessage) => {
                try {
                    const entry: ClipboardDto = JSON.parse(message.body);
                    console.log('Clipboard update received:', entry);
                    onUpdateEntry(entry);
                } catch (error) {
                    console.error('Error parsing update:', error);
                }
            });

            // Subscribe to deletions
            client.subscribe('/topic/clipboard/delete', (message: IMessage) => {
                try {
                    const entryId = parseInt(message.body);
                    console.log('Clipboard deletion received:', entryId);
                    onDeleteEntry(entryId);
                } catch (error) {
                    console.error('Error parsing deletion:', error);
                }
            });

            // Subscribe to clear events
            client.subscribe('/topic/clipboard/clear', (message: IMessage) => {
                try {
                    const clearEvent = JSON.parse(message.body);
                    console.log('Clipboard clear received:', clearEvent);
                    onClear(clearEvent.includePinned);
                } catch (error) {
                    console.error('Error parsing clear event:', error);
                }
            });

            // Subscribe to pong messages for keep-alive
            client.subscribe('/topic/clipboard/pong', (message: IMessage) => {
                console.log('Pong received:', message.body);
            });

            // Send initial ping
            sendPing();
        };

        client.onStompError = (frame) => {
            console.error('STOMP error:', frame.headers['message']);
            console.error('Additional details:', frame.body);
            isConnectedRef.current = false;
            setIsConnected(false);
            toast.error('WebSocket connection error');
        };

        client.onWebSocketClose = () => {
            console.log('WebSocket connection closed');
            isConnectedRef.current = false;
            setIsConnected(false);
        };

        client.onDisconnect = () => {
            console.log('Disconnected from WebSocket');
            isConnectedRef.current = false;
            setIsConnected(false);
        };

        const sendPing = () => {
            if (client.connected) {
                client.publish({
                    destination: '/app/clipboard/ping',
                    body: 'ping'
                });
            }
        };

        // Setup ping interval
        const pingInterval = setInterval(() => {
            sendPing();
        }, 30000); // Send ping every 30 seconds

        // Store ping interval in client for cleanup
        (client as any).pingInterval = pingInterval;

        client.activate();
        clientRef.current = client;
    }, [enabled, onNewEntry, onUpdateEntry, onDeleteEntry, onClear, setIsConnected]);

    const disconnect = useCallback(() => {
        if (clientRef.current) {
            // Clear ping interval
            const pingInterval = (clientRef.current as any).pingInterval;
            if (pingInterval) {
                clearInterval(pingInterval);
            }

            clientRef.current.deactivate();
            clientRef.current = null;
            isConnectedRef.current = false;
            setIsConnected(false);
        }
    }, [setIsConnected]);

    // Effect to manage connection
    useEffect(() => {
        if (enabled) {
            connect();
        } else {
            disconnect();
        }

        return () => {
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
            disconnect();
        };
    }, [enabled, connect, disconnect]);

    const sendMessage = useCallback((destination: string, body: any) => {
        if (clientRef.current?.connected) {
            clientRef.current.publish({
                destination,
                body: JSON.stringify(body)
            });
        } else {
            console.warn('Cannot send message: WebSocket not connected');
        }
    }, []);

    return {
        isConnected,
        sendMessage,
        reconnect: connect,
        disconnect
    };
}