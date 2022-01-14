package knu.myhealthhub.blockchainregistry.validator;

import static knu.myhealthhub.enums.ERROR_CODE.XDS_MISSING_DOCUMENT_METADATA;
import static knu.myhealthhub.settings.Configuration.TRUE;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;

import knu.myhealthhub.datamodels.Metadata;

public class MetaValidator {
    public static String isValidMetadata(Metadata metadata) {
        if (null == metadata.getRepositoryUniqueId()) {
            String reason = String.format("Fail to find key[repositoryUniqueId] from %s", metadata);
            return setErrorMessage(XDS_MISSING_DOCUMENT_METADATA, reason);
        }
        if (null == metadata.getUri()) {
            String reason = String.format("Fail to find key[uri] from %s", metadata);
            return setErrorMessage(XDS_MISSING_DOCUMENT_METADATA, reason);
        }
        return TRUE;
    }
}