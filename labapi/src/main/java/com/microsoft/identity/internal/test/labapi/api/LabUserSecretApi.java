/*
 * Azure Identity Labs API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.microsoft.identity.internal.test.labapi.api;

import com.microsoft.identity.internal.test.labapi.ApiCallback;
import com.microsoft.identity.internal.test.labapi.ApiClient;
import com.microsoft.identity.internal.test.labapi.ApiException;
import com.microsoft.identity.internal.test.labapi.ApiResponse;
import com.microsoft.identity.internal.test.labapi.Configuration;
import com.microsoft.identity.internal.test.labapi.Pair;
import com.microsoft.identity.internal.test.labapi.ProgressRequestBody;
import com.microsoft.identity.internal.test.labapi.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import com.microsoft.identity.internal.test.labapi.model.CustomErrorResponse;
import com.microsoft.identity.internal.test.labapi.model.SecretResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabUserSecretApi {
    private ApiClient apiClient;

    public LabUserSecretApi() {
        this(Configuration.getDefaultApiClient());
    }

    public LabUserSecretApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for apiLabUserSecretGet
     * @param secret Enter the Lab Name as the Secret Param (optional)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call apiLabUserSecretGetCall(String secret, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/api/LabUserSecret";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (secret != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("secret", secret));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call apiLabUserSecretGetValidateBeforeCall(String secret, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        com.squareup.okhttp.Call call = apiLabUserSecretGetCall(secret, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Use LabSecret EndPoint Instead of this. This will be deprecated end of March 2020  You need to provide the secret in Query String.
     * If not found it will return the KeyVault providers generic error message &#x27;not found&#x27;
     * @param secret Enter the Lab Name as the Secret Param (optional)
     * @return SecretResponse
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SecretResponse apiLabUserSecretGet(String secret) throws ApiException {
        ApiResponse<SecretResponse> resp = apiLabUserSecretGetWithHttpInfo(secret);
        return resp.getData();
    }

    /**
     * Use LabSecret EndPoint Instead of this. This will be deprecated end of March 2020  You need to provide the secret in Query String.
     * If not found it will return the KeyVault providers generic error message &#x27;not found&#x27;
     * @param secret Enter the Lab Name as the Secret Param (optional)
     * @return ApiResponse&lt;SecretResponse&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SecretResponse> apiLabUserSecretGetWithHttpInfo(String secret) throws ApiException {
        com.squareup.okhttp.Call call = apiLabUserSecretGetValidateBeforeCall(secret, null, null);
        Type localVarReturnType = TypeToken.get(SecretResponse.class).getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Use LabSecret EndPoint Instead of this. This will be deprecated end of March 2020  You need to provide the secret in Query String. (asynchronously)
     * If not found it will return the KeyVault providers generic error message &#x27;not found&#x27;
     * @param secret Enter the Lab Name as the Secret Param (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call apiLabUserSecretGetAsync(String secret, final ApiCallback<SecretResponse> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = apiLabUserSecretGetValidateBeforeCall(secret, progressListener, progressRequestListener);
        Type localVarReturnType = TypeToken.get(SecretResponse.class).getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}