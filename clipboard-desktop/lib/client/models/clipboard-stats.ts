/* tslint:disable */
/* eslint-disable */
/**
 * Clipboard API
 * REST API for managing clipboard entries
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */



/**
 * 
 * @export
 * @interface ClipboardStats
 */
export interface ClipboardStats {
    /**
     * 
     * @type {number}
     * @memberof ClipboardStats
     */
    'totalEntries'?: number;
    /**
     * 
     * @type {number}
     * @memberof ClipboardStats
     */
    'pinnedEntries'?: number;
    /**
     * 
     * @type {number}
     * @memberof ClipboardStats
     */
    'unpinnedEntries'?: number;
    /**
     * 
     * @type {string}
     * @memberof ClipboardStats
     */
    'oldestEntry'?: string;
    /**
     * 
     * @type {string}
     * @memberof ClipboardStats
     */
    'newestEntry'?: string;
}

