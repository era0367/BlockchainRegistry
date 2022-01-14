package knu.myhealthhub.blockchainregistry.validator;

import static knu.myhealthhub.blockchainregistry.validator.MetaValidator.isValidMetadata;
import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.enums.ERROR_CODE.*;
import static knu.myhealthhub.settings.Configuration.TRUE;
import static knu.myhealthhub.settings.KeyString.KEY_FOR_META_LIST;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;

import knu.myhealthhub.datamodels.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ParamValidator {
    public static String validateParametersForITI42(String body) {
        JSONObject parameter = toJsonObject(body);
        if (null == parameter) {
            return setErrorMessage(XDS_MISSING_DOCUMENT, null);
        }
        JSONArray metadataList = getJsonArray(parameter, KEY_FOR_META_LIST);
        if (null == metadataList) {
            return setErrorMessage(XDS_MISSING_DOCUMENT_METADATA, null);
        }
        return validateMetadataSet(metadataList);
    }
    private static String validateMetadataSet(JSONArray metadataList) {
        for (int i = 0; i < metadataList.size(); i++) {
            JSONObject metadataJson = getJsonObjectFromArray(metadataList, i);
            String validateDocumentEntryResult = validateMetadataEntry(metadataJson);
            if (!validateDocumentEntryResult.equals(TRUE)) {
                return validateDocumentEntryResult;
            }
        }
        return TRUE;
    }
    private static String validateMetadataEntry(JSONObject metadataJson) {
        Metadata metadata = toJavaObject(metadataJson, Metadata.class);
        if (null == metadata) {
            String reason = String.format("Fail to parse JSON to JavaObject[Metadata] %s", metadataJson.toJSONString());
            return setErrorMessage(XDS_REPOSITORY_METADATA_ERROR, reason);
        }
        return isValidMetadata(metadata);
    }
    public static String validateParametersForITI18(RegistryStoredQueryRequest registryStoredQueryRequest) {
        //@Todo: adhocQuery check
        return isValidParametersForTime(registryStoredQueryRequest.getRequestData());
    }
    public static String validateParametersForREG01(RequestData requestData) {
        return isValidParametersForTime(requestData);
    }
    private static String isValidParametersForTime(RequestData requestData) {
        if ((null != requestData.getCreationTimeFrom()) && (null != requestData.getCreationTimeTo())) {
            return setErrorMessage(XDS_STORED_QUERY_MISSING_PARAM, null);
        }
        return TRUE;
    }
}