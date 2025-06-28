# ClipboardExportApi

All URIs are relative to *http://localhost:5000*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**exportAsCsv**](#exportascsv) | **GET** /api/local/clipboards/export/csv | Export clipboard entries as CSV|
|[**exportAsJson**](#exportasjson) | **GET** /api/local/clipboards/export/json | Export clipboard entries as JSON|
|[**exportAsText**](#exportastext) | **GET** /api/local/clipboards/export/txt | Export clipboard entries as text|

# **exportAsCsv**
> exportAsCsv()

Exports clipboard entries in CSV format

### Example

```typescript
import {
    ClipboardExportApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardExportApi(configuration);

let includePinned: boolean; //Whether to include pinned entries in export (optional) (default to true)

const { status, data } = await apiInstance.exportAsCsv(
    includePinned
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **includePinned** | [**boolean**] | Whether to include pinned entries in export | (optional) defaults to true|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/csv, */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully exported clipboard entries |  -  |
|**500** | Export failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **exportAsJson**
> exportAsJson()

Exports clipboard entries in JSON format

### Example

```typescript
import {
    ClipboardExportApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardExportApi(configuration);

let includePinned: boolean; //Whether to include pinned entries in export (optional) (default to true)

const { status, data } = await apiInstance.exportAsJson(
    includePinned
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **includePinned** | [**boolean**] | Whether to include pinned entries in export | (optional) defaults to true|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully exported clipboard entries |  -  |
|**500** | Export failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **exportAsText**
> exportAsText()

Exports clipboard entries in plain text format

### Example

```typescript
import {
    ClipboardExportApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardExportApi(configuration);

let includePinned: boolean; //Whether to include pinned entries in export (optional) (default to true)

const { status, data } = await apiInstance.exportAsText(
    includePinned
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **includePinned** | [**boolean**] | Whether to include pinned entries in export | (optional) defaults to true|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain, */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully exported clipboard entries |  -  |
|**500** | Export failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

