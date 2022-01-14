package knu.myhealthhub.blockchainregistry.transactions;

import static knu.myhealthhub.blockchainregistry.BlockchainRegistryApplication.logger;
import static knu.myhealthhub.blockchainregistry.authentication.AccessToken.getAccessToken;
import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_REGISTRY_ERROR;
import static knu.myhealthhub.settings.Configuration.*;
import static knu.myhealthhub.settings.KeyString.*;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;
import static knu.myhealthhub.transactions.RestSender.createRest;
import static knu.myhealthhub.transactions.RestSender.getHeader;

import knu.myhealthhub.datamodels.Metadata;
import knu.myhealthhub.datamodels.RegistryError;
import knu.myhealthhub.enums.USER_TYPE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

public class ITI42 {
    public static String registerDocumentSet(String body) {
        JSONObject parameter = toJsonObject(body);
        assert parameter != null;
        JSONArray metadataList = getJsonArray(parameter, KEY_FOR_META_LIST);
        JSONArray registryErrorList = new JSONArray();
        String status = SUCCESS;
        JSONObject result = new JSONObject();
        for (int i = 0; i < metadataList.size(); i++) {
            JSONObject metadataJson = getJsonObjectFromArray(metadataList, i);
            Metadata metadata = toJavaObject(metadataJson, Metadata.class);
            String registerDocumentSetResult = registerMetadata(metadata);
            if (!registerDocumentSetResult.equals(TRUE)) {
                status = FAILURE;
                JSONObject registerDocumentSetResultJson = toJsonObject(registerDocumentSetResult);
                registryErrorList.add(registerDocumentSetResultJson);
            }
        }
        result.put(KEY_FOR_STATUS, status);
        result.put(KEY_FOR_REGISTRY_ERROR_LIST, registryErrorList);
        return result.toJSONString();
    }
    private static String registerMetadata(Metadata metadata) {
        String metadataJson = toJsonObjectFromJavaObject(metadata);
        String response = requestRegisterData(metadataJson);
        if (null == response) {
            return setErrorMessage(XDS_REGISTRY_ERROR, null);
        }
        JSONObject responseJson = toJsonObject(response);
        if (null != toJavaObject(responseJson, RegistryError.class)) {
            return response;
        }
        return TRUE;
    }
    private static String requestRegisterData(String metadata) {
        String url = BLOCKCHAIN + URL_DEFAULT + METADATA;
        String getAccessTokenResult = getAccessToken(USER_TYPE.DATA_CONSUMER);
        if (isJsonObject(getAccessTokenResult)) {
            return getAccessTokenResult;
        }
        JSONObject body = toJsonObject(metadata);
        String result = createRest(url, HttpMethod.POST, getHeader(getAccessTokenResult), body);
        logger.debug("[REQUEST::]" + url);
        logger.debug("\t\t > " + body);
        logger.debug("\t\t < " + result);
        return result;
    }
}