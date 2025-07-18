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


import type { Configuration } from '../configuration';
import type { AxiosPromise, AxiosInstance, RawAxiosRequestConfig } from 'axios';
import globalAxios from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { DUMMY_BASE_URL, assertParamExists, setApiKeyToObject, setBasicAuthToObject, setBearerAuthToObject, setOAuthToObject, setSearchParams, serializeDataIfNeeded, toPathString, createRequestFunction } from '../common';
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, RequestArgs, BaseAPI, RequiredError, operationServerMap } from '../base';
/**
 * HealthApi - axios parameter creator
 * @export
 */
export const HealthApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Returns the health status of the API
         * @summary Health check
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        health: async (options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/api/health`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * HealthApi - functional programming interface
 * @export
 */
export const HealthApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = HealthApiAxiosParamCreator(configuration)
    return {
        /**
         * Returns the health status of the API
         * @summary Health check
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async health(options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<{ [key: string]: string; }>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.health(options);
            const index = configuration?.serverIndex ?? 0;
            const operationBasePath = operationServerMap['HealthApi.health']?.[index]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, operationBasePath || basePath);
        },
    }
};

/**
 * HealthApi - factory interface
 * @export
 */
export const HealthApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = HealthApiFp(configuration)
    return {
        /**
         * Returns the health status of the API
         * @summary Health check
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        health(options?: any): AxiosPromise<{ [key: string]: string; }> {
            return localVarFp.health(options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * HealthApi - interface
 * @export
 * @interface HealthApi
 */
export interface HealthApiInterface {
    /**
     * Returns the health status of the API
     * @summary Health check
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HealthApiInterface
     */
    health(options?: RawAxiosRequestConfig): AxiosPromise<{ [key: string]: string; }>;

}

/**
 * HealthApi - object-oriented interface
 * @export
 * @class HealthApi
 * @extends {BaseAPI}
 */
export class HealthApi extends BaseAPI implements HealthApiInterface {
    /**
     * Returns the health status of the API
     * @summary Health check
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HealthApi
     */
    public health(options?: RawAxiosRequestConfig) {
        return HealthApiFp(this.configuration).health(options).then((request) => request(this.axios, this.basePath));
    }
}

