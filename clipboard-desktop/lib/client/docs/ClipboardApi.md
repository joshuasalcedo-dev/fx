# ClipboardApi

All URIs are relative to *http://localhost:5000*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**clipboards**](#clipboards) | **GET** /api/local/clipboards | Get all clipboard entries|
|[**deleteAllClipboards**](#deleteallclipboards) | **DELETE** /api/local/clipboards/delete | Delete all clipboard entries|
|[**deleteClipboard**](#deleteclipboard) | **DELETE** /api/local/clipboards/delete/{id} | Delete a clipboard entry|
|[**getClipboardStats**](#getclipboardstats) | **GET** /api/local/clipboards/stats | |
|[**getRecentClipboards**](#getrecentclipboards) | **GET** /api/local/clipboards/recent | |
|[**pinClipboards**](#pinclipboards) | **PUT** /api/local/clipboards/pin | Toggle pin status|
|[**pinnedClipboards**](#pinnedclipboards) | **GET** /api/local/clipboards/pins | Get pinned clipboard entries|
|[**searchClipboards**](#searchclipboards) | **GET** /api/local/clipboards/search | Search clipboard entries|
|[**stopClipboard**](#stopclipboard) | **POST** /api/local/clipboards/stop | |

# **clipboards**
> Page clipboards()

Returns a paginated list of clipboard entries sorted by timestamp in descending order

### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let page: number; //Page number (zero-based) (optional) (default to 0)
let max: number; //Number of items per page (max 100) (optional) (default to 20)

const { status, data } = await apiInstance.clipboards(
    page,
    max
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **page** | [**number**] | Page number (zero-based) | (optional) defaults to 0|
| **max** | [**number**] | Number of items per page (max 100) | (optional) defaults to 20|


### Return type

**Page**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved clipboard entries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteAllClipboards**
> deleteAllClipboards()

Deletes all clipboard entries, optionally including pinned entries

### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let includePinned: boolean; //Whether to include pinned entries in deletion (optional) (default to false)

const { status, data } = await apiInstance.deleteAllClipboards(
    includePinned
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **includePinned** | [**boolean**] | Whether to include pinned entries in deletion | (optional) defaults to false|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Successfully deleted clipboard entries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteClipboard**
> deleteClipboard()

Deletes a specific clipboard entry by ID

### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let id: number; //ID of the clipboard entry to delete (default to undefined)

const { status, data } = await apiInstance.deleteClipboard(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] | ID of the clipboard entry to delete | defaults to undefined|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Successfully deleted the clipboard entry |  -  |
|**404** | Clipboard entry not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getClipboardStats**
> ClipboardStats getClipboardStats()


### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

const { status, data } = await apiInstance.getClipboardStats();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**ClipboardStats**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRecentClipboards**
> Array<ClipboardDto> getRecentClipboards()


### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let hours: number; // (optional) (default to 24)

const { status, data } = await apiInstance.getRecentClipboards(
    hours
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **hours** | [**number**] |  | (optional) defaults to 24|


### Return type

**Array<ClipboardDto>**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **pinClipboards**
> ClipboardDto pinClipboards(togglePinRequest)

Toggles the pin status of a clipboard entry

### Example

```typescript
import {
    ClipboardApi,
    Configuration,
    TogglePinRequest
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let togglePinRequest: TogglePinRequest; //

const { status, data } = await apiInstance.pinClipboards(
    togglePinRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **togglePinRequest** | **TogglePinRequest**|  | |


### Return type

**ClipboardDto**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully toggled pin status |  -  |
|**404** | Clipboard entry not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **pinnedClipboards**
> string pinnedClipboards()

Returns a list of all pinned clipboard entries

### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

const { status, data } = await apiInstance.pinnedClipboards();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**string**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved pinned clipboard entries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **searchClipboards**
> Page searchClipboards()

Searches clipboard entries by content

### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

let query: string; //Search query (default to undefined)
let page: number; //Page number (zero-based) (optional) (default to 0)
let size: number; //Number of items per page (max 100) (optional) (default to 20)

const { status, data } = await apiInstance.searchClipboards(
    query,
    page,
    size
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **query** | [**string**] | Search query | defaults to undefined|
| **page** | [**number**] | Page number (zero-based) | (optional) defaults to 0|
| **size** | [**number**] | Number of items per page (max 100) | (optional) defaults to 20|


### Return type

**Page**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved search results |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **stopClipboard**
> StopClipboardResponse stopClipboard()


### Example

```typescript
import {
    ClipboardApi,
    Configuration
} from 'clipboard-api-client';

const configuration = new Configuration();
const apiInstance = new ClipboardApi(configuration);

const { status, data } = await apiInstance.stopClipboard();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**StopClipboardResponse**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

