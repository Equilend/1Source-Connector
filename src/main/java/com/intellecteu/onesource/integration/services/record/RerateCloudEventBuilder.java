package com.intellecteu.onesource.integration.services.record;

import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.ContractInitiation.DataMsg.GET_COUNTERPARTY_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.GET_RERATE_EXCEPTION_1SOURCE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.MATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.DataMsg.UNMATCHED_RERATE_MSG;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.GET_RERATE_EXCEPTION_1SOURCE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.MATCHED_RERATE;
import static com.intellecteu.onesource.integration.constant.RecordMessageConstant.Rerate.Subject.UNMATCHED_RERATE;
import static com.intellecteu.onesource.integration.enums.IntegrationProcess.RERATE;
import static com.intellecteu.onesource.integration.enums.IntegrationSubProcess.GET_RERATE_PROPOSAL;
import static com.intellecteu.onesource.integration.enums.RecordType.RERATE_PROPOSAL_MATCHED_RERATE_TRADE;
import static com.intellecteu.onesource.integration.enums.RecordType.RERATE_PROPOSAL_UNMATCHED;
import static com.intellecteu.onesource.integration.enums.RecordType.TECHNICAL_EXCEPTION_1SOURCE;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.record.CloudEventBuildRequest;
import com.intellecteu.onesource.integration.dto.record.RelatedObject;
import com.intellecteu.onesource.integration.enums.IntegrationProcess;
import com.intellecteu.onesource.integration.enums.IntegrationSubProcess;
import com.intellecteu.onesource.integration.enums.RecordType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class RerateCloudEventBuilder extends IntegrationCloudEventBuilder {

    @Override
    public IntegrationProcess getVersion() {
        return RERATE;
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String recorded) {
        return switch (subProcess) {
            case GET_RERATE_PROPOSAL -> createGetRerateExceptionCloudRequest(exception, subProcess, recorded);
            default -> null;
        };
    }

    private CloudEventBuildRequest createGetRerateExceptionCloudRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess, String recorded) {
        String dataMessage = format(GET_RERATE_EXCEPTION_1SOURCE_MSG, recorded, exception.getStatusText());
        return createRecordRequest(
            TECHNICAL_EXCEPTION_1SOURCE,
            format(GET_RERATE_EXCEPTION_1SOURCE, recorded),
            RERATE,
            subProcess,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildRequest(String recorded, RecordType recordType, String related) {
        return switch (recordType) {
            case RERATE_PROPOSAL_MATCHED_RERATE_TRADE -> createMatchedRerateRecordRequest(recorded, related);
            case RERATE_PROPOSAL_UNMATCHED -> createUnMatchedRerateRecordRequest(recorded);
            default -> null;
        };
    }


    /**
     * @param recorded - onesource rerate id
     * @param related - backoffice rerate id
     * @return
     */
    private CloudEventBuildRequest createMatchedRerateRecordRequest(String recorded, String related) {
        String dataMessage = format(MATCHED_RERATE_MSG, recorded, related);
        return createRecordRequest(
            RERATE_PROPOSAL_MATCHED_RERATE_TRADE,
            format(MATCHED_RERATE, related),
            RERATE,
            GET_RERATE_PROPOSAL,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    /**
     * @param recorded - onesource rerate id
     * @return
     */
    private CloudEventBuildRequest createUnMatchedRerateRecordRequest(String recorded) {
        String dataMessage = format(UNMATCHED_RERATE_MSG, recorded);
        return createRecordRequest(
            RERATE_PROPOSAL_UNMATCHED,
            format(UNMATCHED_RERATE, recorded),
            RERATE,
            GET_RERATE_PROPOSAL,
            createEventData(dataMessage, List.of(RelatedObject.notApplicable()))
        );
    }

    @Override
    public CloudEventBuildRequest buildExceptionRequest(HttpStatusCodeException exception,
        IntegrationSubProcess subProcess) {
        return null;
    }
}
