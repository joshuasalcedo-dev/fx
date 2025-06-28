// Type declarations for chart components
import { TooltipProps } from 'recharts';

declare module 'recharts' {
  export interface TooltipPayload<TValue = any, TName = any> {
    payload?: any;
    value?: TValue;
    name?: TName;
    dataKey?: string | number;
    color?: string;
    [key: string]: any;
  }
}

declare global {
  interface ChartConfig {
    [key: string]: {
      label: string;
      color?: string;
      icon?: React.ComponentType;
      theme?: {
        light?: string;
        dark?: string;
      };
    };
  }
}

export {};