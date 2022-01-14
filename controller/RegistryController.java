package knu.myhealthhub.blockchainregistry.controller;

import static knu.myhealthhub.blockchainregistry.transactions.ITI18.registryStoredQuery;
import static knu.myhealthhub.blockchainregistry.transactions.ITI42.registerDocumentSet;
import static knu.myhealthhub.blockchainregistry.transactions.REG02.retrieveMetadataById;
import static knu.myhealthhub.blockchainregistry.validator.ParamValidator.*;
import static knu.myhealthhub.common.JsonUtility.toJavaObject;
import static knu.myhealthhub.common.JsonUtility.toJsonObject;
import static knu.myhealthhub.settings.Configuration.*;

import knu.myhealthhub.datamodels.RegistryStoredQueryRequest;
import knu.myhealthhub.datamodels.RequestData;
import org.json.simple.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistryController {
    @PostMapping(URL_DEFAULT + METADATA)
    public String xdsRegisterDocumentSet(@RequestBody @Validated String body) {
        String validateParamResult = validateParametersForITI42(body);
        if (!validateParamResult.equals(TRUE)) {
            return validateParamResult;
        }
        return registerDocumentSet(body);
    }
    @GetMapping(URL_DEFAULT + METADATA + QUERY)
    public String xdsRegistryStoredQuery(@RequestParam("registryStoredQueryRequest") String registryStoredQueryRequestString) {
        JSONObject registryStoredQueryRequestJson = toJsonObject(registryStoredQueryRequestString);
        RegistryStoredQueryRequest registryStoredQueryRequest = toJavaObject(registryStoredQueryRequestJson, RegistryStoredQueryRequest.class);
        String validateParamResult = validateParametersForITI18(registryStoredQueryRequest);
        if (!validateParamResult.equals(TRUE)) {
            return validateParamResult;
        }
        return registryStoredQuery(registryStoredQueryRequest.getRequestData());
    }
    @GetMapping(URL_DEFAULT + METADATA)
    public String regRequestData(@RequestParam("dataSubjectId") String dataSubjectId, @RequestParam(required = false) String creationTimeFrom, @RequestParam(required = false) String creationTimeTo) {
        RequestData requestData = new RequestData();
        requestData.setDataSubjectId(dataSubjectId);
        requestData.setCreationTimeFrom(creationTimeFrom);
        requestData.setCreationTimeTo(creationTimeTo);
        String validateParamResult = validateParametersForREG01(requestData);
        if (!validateParamResult.equals(TRUE)) {
            return validateParamResult;
        }
        return registryStoredQuery(requestData);
    }
    @GetMapping(URL_DEFAULT + METADATA + ID)
    public String cdcRequestDataById(@PathVariable("id") String id) {
        return retrieveMetadataById(id);
    }
}
