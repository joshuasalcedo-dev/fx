## clipboard-api-client@1.0.0

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install clipboard-api-client@1.0.0 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost:5000*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*ClipboardApi* | [**clipboards**](docs/ClipboardApi.md#clipboards) | **GET** /api/local/clipboards | Get all clipboard entries
*ClipboardApi* | [**deleteAllClipboards**](docs/ClipboardApi.md#deleteallclipboards) | **DELETE** /api/local/clipboards/delete | Delete all clipboard entries
*ClipboardApi* | [**deleteClipboard**](docs/ClipboardApi.md#deleteclipboard) | **DELETE** /api/local/clipboards/delete/{id} | Delete a clipboard entry
*ClipboardApi* | [**getClipboardStats**](docs/ClipboardApi.md#getclipboardstats) | **GET** /api/local/clipboards/stats | 
*ClipboardApi* | [**getRecentClipboards**](docs/ClipboardApi.md#getrecentclipboards) | **GET** /api/local/clipboards/recent | 
*ClipboardApi* | [**pinClipboards**](docs/ClipboardApi.md#pinclipboards) | **PUT** /api/local/clipboards/pin | Toggle pin status
*ClipboardApi* | [**pinnedClipboards**](docs/ClipboardApi.md#pinnedclipboards) | **GET** /api/local/clipboards/pins | Get pinned clipboard entries
*ClipboardApi* | [**searchClipboards**](docs/ClipboardApi.md#searchclipboards) | **GET** /api/local/clipboards/search | Search clipboard entries
*ClipboardApi* | [**stopClipboard**](docs/ClipboardApi.md#stopclipboard) | **POST** /api/local/clipboards/stop | 
*ClipboardExportApi* | [**exportAsCsv**](docs/ClipboardExportApi.md#exportascsv) | **GET** /api/local/clipboards/export/csv | Export clipboard entries as CSV
*ClipboardExportApi* | [**exportAsJson**](docs/ClipboardExportApi.md#exportasjson) | **GET** /api/local/clipboards/export/json | Export clipboard entries as JSON
*ClipboardExportApi* | [**exportAsText**](docs/ClipboardExportApi.md#exportastext) | **GET** /api/local/clipboards/export/txt | Export clipboard entries as text
*HealthApi* | [**health**](docs/HealthApi.md#health) | **GET** /api/health | Health check


### Documentation For Models

 - [ClipboardDto](docs/ClipboardDto.md)
 - [ClipboardStats](docs/ClipboardStats.md)
 - [Page](docs/Page.md)
 - [PageableObject](docs/PageableObject.md)
 - [SortObject](docs/SortObject.md)
 - [StopClipboardResponse](docs/StopClipboardResponse.md)
 - [TogglePinRequest](docs/TogglePinRequest.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization

Endpoints do not require authorization.

