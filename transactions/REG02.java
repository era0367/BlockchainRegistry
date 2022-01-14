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

import knu.myhealthhub.enums.USER_TYPE;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

public class REG02 {
    public static String retrieveMetadataById(String metadataId) {
        String requestQueryResult = requestQueryById(metadataId);
        if (null == requestQueryResult) {
            return setErrorMessage(XDS_REGISTRY_ERROR, null);
        }
        JSONObject requestQueryResultJson = toJsonObject(requestQueryResult);
        JSONObject resultJson = getJsonObject(requestQueryResultJson, KEY_FOR_RESULT);
        String resultCode = getStringFromObject(resultJson, KEY_FOR_RESULT_CODE);
        if (!resultCode.equalsIgnoreCase(SUCCESS)) {
            return setErrorMessage(XDS_REGISTRY_ERROR, resultJson.toJSONString());
        }
        JSONObject metadataJson = getJsonObject(requestQueryResultJson, KEY_FOR_METADATA);
        metadataJson.remove(KEY_FOR_DOCTYPE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_FOR_STATUS, SUCCESS);
        jsonObject.put(KEY_FOR_METADATA, metadataJson);
        return jsonObject.toJSONString();
    }
    public static String requestQueryById(String metadataId) {
        String url = String.format("%s%s%s/%s", BLOCKCHAIN, URL_DEFAULT, METADATA, metadataId);
        String getAccessTokenResult = getAccessToken(USER_TYPE.DATA_SUBJECT);
        if (isJsonObject(getAccessTokenResult)) {
            return getAccessTokenResult;
        }
        String result = createRest(url, HttpMethod.GET, getHeader(getAccessTokenResult), new JSONObject());
        logger.debug("[REQUEST::]" + url);
        logger.debug("\t\t > ");
        logger.debug("\t\t < " + result);
        return result;
    }
}
