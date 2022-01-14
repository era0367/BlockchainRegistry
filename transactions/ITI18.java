package knu.myhealthhub.blockchainregistry.transactions;

import static knu.myhealthhub.blockchainregistry.BlockchainRegistryApplication.logger;
import static knu.myhealthhub.blockchainregistry.authentication.AccessToken.getAccessToken;
import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.enums.AVAILABILITY_STATUS.AVAILABLE;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_REGISTRY_ERROR;
import static knu.myhealthhub.enums.USER_TYPE.DATA_CONSUMER;
import static knu.myhealthhub.settings.Configuration.*;
import static knu.myhealthhub.settings.KeyString.*;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;
import static knu.myhealthhub.transactions.RestSender.createRest;
import static knu.myhealthhub.transactions.RestSender.getHeader;

import knu.myhealthhub.datamodels.RequestData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

public class ITI18 {
    public static String registryStoredQuery(RequestData requestData) {
        String requestQueryResult = requestQuery(requestData);
        if (null == requestQueryResult) {
            return setErrorMessage(XDS_REGISTRY_ERROR, null);
        }
        JSONObject requestQueryResultJson = toJsonObject(requestQueryResult);
        JSONObject resultJson = getJsonObject(requestQueryResultJson, KEY_FOR_RESULT);
        String resultCode = getStringFromObject(resultJson, KEY_FOR_RESULT_CODE);
        if (!resultCode.equalsIgnoreCase(SUCCESS)) {
            return setErrorMessage(XDS_REGISTRY_ERROR, resultJson.toJSONString());
        }
        JSONArray jsonArray = getJsonArray(requestQueryResultJson, KEY_FOR_METADATA_LIST);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_FOR_STATUS, SUCCESS);
        jsonObject.put(KEY_FOR_META_LIST, jsonArray);
        return jsonObject.toJSONString();
    }
    public static String requestQuery(RequestData requestData) {
        String url = BLOCKCHAIN + URL_DEFAULT + METADATA;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam(KEY_FOR_PATIENT_ID, requestData.getDataSubjectId()).queryParam(KEY_FOR_STATUS, AVAILABLE);
        if (null != requestData.getCreationTimeFrom()) {
            builder.queryParam(KEY_FOR_CREATION_TIME_FROM, requestData.getCreationTimeFrom()).queryParam(KEY_FOR_CREATION_TIME_TO, requestData.getCreationTimeTo());
        }
        String getAccessTokenResult = getAccessToken(DATA_CONSUMER);
        if (isJsonObject(getAccessTokenResult)) {
            return getAccessTokenResult;
        }
        String result = createRest(builder.toUriString(), HttpMethod.GET, getHeader(getAccessTokenResult), new JSONObject());
        logger.debug("[REQUEST::]" + builder.toUriString());
        logger.debug("\t\t > ");
        logger.debug("\t\t < " + result);
        return result;
    }
}