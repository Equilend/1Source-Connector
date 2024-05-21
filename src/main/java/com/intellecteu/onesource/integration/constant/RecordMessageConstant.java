package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordMessageConstant {

    public static final String NOT_APPLICABLE = "N/A";

    public static class ContractCancellation {

        public static class Subject {

            public static final String CAPTURE_POSITION_CANCELED_EXCEPTION_SUBJECT = """
                Capture of canceled positions - %s""";
            public static final String INSTRUCT_CONTRACT_CANCEL_SUBJECT = "Position - %s";
            public static final String LOAN_CONTRACT_CANCEL_PENDING_SUBJECT = "Position - %s";
            public static final String LOAN_CONTRACT_CANCELED_SUBJECT = "Position - %s";
            public static final String POSITION_CANCEL_SUBMITTED_SUBJECT = "Position - %s";
            public static final String PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_SUBJECT = "1Source Contract - %s";
        }

        public static class DataMsg {

            public static final String CAPTURE_POSITION_CANCELED_EXCEPTION_MSG = """
                Canceled positions cannot be captured from SPIRE for the following reason: %s""";

            public static final String INSTRUCT_CONTRACT_CANCEL_MSG = """
                The loan contract: %s (SPIRE position: %s) cannot be cancelled for the following reason: %s""";
            public static final String LOAN_CONTRACT_CANCELED_MSG = """
                The loan contract proposal %s matching with the SPIRE position %s has been canceled.""";

            public static final String LOAN_CONTRACT_CANCEL_PENDING_MSG = """
                The loan contract proposal %s matching with the SPIRE position %s is pending canceled \
                as per counterparty cancellation request.""";
            public static final String POSITION_CANCEL_SUBMITTED_MSG = """
                The position %s related to 1Source loan contract %s has been canceled.""";

            public static final String PROCESS_LOAN_CONTRACT_PENDING_CANCELLATION_MSG = """
                The loan contract %s has been marked as pending canceled in 1Source \
                but the loan contract pending settlement has not been retrieved in the Integration toolkit.""";
        }
    }

    public static class ContractInitiation {

        public static class Subject {

            public static final String APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_SUBJECT = "Position - %s";
            public static final String CONFIRM_POSITION_SUBJECT = "Position - %s";
            public static final String GET_AGREEMENT_EXCEPTION_1SOURCE = "1Source event id - %s";
            public static final String GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION_SUBJECT = "Position - %s";

            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED = "Loan contract proposal - %s";
            public static final String GET_LOAN_CONTRACT_PROPOSAL_DISCREPANCIES = "Position - %s";
            public static final String GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE = """
                Extract of new positions pending conf. - %s""";
            public static final String GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE = "Position - %s";
            public static final String LOAN_CONTRACT_MATCHED_POSITION = "Position - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_MATCHED_DECLINED_SUBJECT = "Position - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_NOT_MATCHED_DECLINED_SUBJECT = "Loan contract proposal - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL_SUBJECT = "Position - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_APPROVED = "Position - %s";
            public static final String GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_SPIRE = """
                Extract of new positions pending conf. - %s""";
            public static final String CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Loan contract proposal - %s";
            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED = "Position - %s";
            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_NO_RELATED_POSITION_CANCELED = "Loan contract proposal - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_UNMATCHED_SUBJECT = "Loan contract proposal - %s";
            public static final String POSITION_CANCELED_SUBJECT = "Position - %s";
            public static final String POSITION_CANCELED_SUBMITTED_SUBJECT = "Position - %s";
            public static final String POSITION_SUBMITTED_SUBJECT = "Position - %s";
            public static final String POSITION_UNMATCHED_SUBJECT = "Position - %s";
            public static final String POSITION_UPDATE_SUBMITTED_SUBJECT = "Position - %s";
            public static final String POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE = "Position - %s";
            public static final String POST_POSITION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
            public static final String POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_APPROVED_SUBJECT = "1Source Contract - %s";
            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_CANCELED_SUBJECT = "1Source Contract - %s";
            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_DECLINED_SUBJECT = "1Source Contract - %s";
            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_SETTLED_SUBJECT = "1Source Contract - %s";
            public static final String TOOLKIT_ISSUE_PROCESS_TRADE_CANCELATION_SUBJECT = "Trade agreement - %s";
            public static final String TRADE_AGREEMENT_DISCREPANCIES = "Position - %s";
            public static final String TRADE_AGREEMENT_RECONCILED = "Position - %s";
            public static final String TRADE_AGREEMENT_CREATED = "1Source trade agreement - %s";
            public static final String TRADE_AGREEMENT_CANCELED = "Shared trade ticket id - %s";
            public static final String TRADE_AGREEMENT_CANCELED_MATCHED_POSITION = "Position - %s";
            public static final String TRADE_AGREEMENT_MATCHED_POSITION = "Position - %s";
            public static final String TRADE_AGREEMENT_UNMATCHED_POSITION_SUBJECT = "Spire trade ticket id - %s";
            public static final String TRADE_AGREEMENT_MATCHED_CANCELED_POSITION = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED = "Position - %s";
            public static final String RERATE_DISCREPANCIES = "Trade - %s";
        }

        public static class DataMsg {

            public static final String APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG = """
                The loan contract %s matching with the position %s cannot be approved for the following reason: %s""";

            public static final String CANCEL_LOAN_PROPOSAL_MSG = """
                The loan contract proposal %s (generated from the SPIRE Position: %s) \
                cannot be canceled for the following reason: %s""";

            public static final String CONFIRM_POSITION_MSG = """
                The position %s have not been confirmed in SPIRE. The identifier \
                of the confirmed 1Source contract is : %s. The reason is: %s""";

            public static final String CONTRACT_CANCEL_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been canceled by the Lender \
                for the following reasons: %s""";

            public static final String CONTRACT_CANCEL_NO_RELATED_POSITION_MSG = """
                The loan contract proposal %s has been canceled by the Lender \
                for the following reasons: %s""";

            public static final String CONTRACT_CREATE_MSG = """
                The loan contract proposal %s has been created but not yet matched with a SPIRE position""";

            public static final String CONTRACT_DECLINE_MATCHED_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been declined by the Borrower \
                for the following reasons: ...""";

            public static final String CONTRACT_DECLINE_NOT_MATCHED_MSG = """
                The loan contract proposal %s has been declined by the Borrower \
                for the following reasons: ...""";

            public static final String GET_COUNTERPARTY_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG = """
                The counterparty settlement instruction for the loan contract %s \
                (SPIRE position: %s) have not been retrieved from SPIRE for the following reason: %s""";

            public static final String DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG = """
                The loan contract %s matching with the position %s cannot be declined for the following reason: %s""";

            public static final String GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG = """
                The details of loan contract proposal %s have not been retrieved \
                from 1Source for the following reason: %s""";

            public static final String GET_NEW_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG = """
                New positions pending confirmation cannot be extracted from SPIRE for the following reason: %s""";

            public static final String GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG = """
                The settlement instruction applicable to the SPIRE position: %s \
                cannot be retrieved from SPIRE for the following reason: %s""";

            public static final String GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG = """
                The details of the trade agreement %s cannot be retrieved \
                from 1Source for the following reason: %s""";

            public static final String LOAN_CONTRACT_PROPOSAL_PENDING_APPROVAL_MSG = """
                The loan contract proposal %s has been matched with a SPIRE position: %s \
                and needs now to be approved by the counterparty.""";

            public static final String LOAN_CONTRACT_PROPOSAL_APPROVE_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been approved by the Borrower""";

            public static final String LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG = """
                The Lender’s loan contract proposal %s has been successfully reconciled \
                with the position %s""";

            public static final String LOAN_CONTRACT_PROPOSAL_VALIDATED_MSG = """
                The Lender’s loan contract proposal %s has been successfully reconciled \
                with the position %s and needs now to be approved.""";

            public static final String GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_MSG = """
                Updated positions pending confirmation cannot be extracted from SPIRE for the following reason: %s""";

            public static final String POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG = """
                The loan contract proposal instruction (generated from the SPIRE position: %s) \
                has not been processed by 1Source for the following reason: %s""";

            public static final String POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG = """
                The loan contract %s cannot be updated for the following reason: %s""";

            public static final String POST_POSITION_UPDATE_EXCEPTION_MSG = """
                The position %s have not been updated in SPIRE with the loan contract \
                identifier: %s for the following reason: %s""";

            public static final String POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG = """
                The counterparty settlement instruction for the loan contract %s \
                (SPIRE position: %s) has not been updated in SPIRE for the following reason: %s""";

            public static final String RECONCILE_LOAN_CONTRACT_DISCREPANCIES_MSG = """
                Discrepancies have been found between the Lender's loan contract proposal %s \
                and the matched SPIRE position %s.""";

            public static final String RECONCILE_TRADE_AGREEMENT_DISCREPANCIES_MSG = """
                Discrepancies have been found between the trade agreement %s and the matched SPIRE position %s""";

            public static final String RECONCILE_RERATE_DISCREPANCIES_MSG = """
                The rerate proposal %s has not been reconciled with a SPIRE rerate trade: %s. Discrepancies must be sort-out""";

            public static final String RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG = """
                The trade agreement %s has been successfully reconciled \
                with the position %s in SPIRE""";

            public static final String LOAN_CONTRACT_PROPOSAL_UNMATCHED_MSG = """
                The loan contract proposal %s has been received but not yet matched with a SPIRE position""";

            public static final String MATCHED_POSITION_TRADE_AGREEMENT_MSG = """
                The trade agreement %s has been matched with a SPIRE position %s""";

            public static final String MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG = """
                The trade agreement %s has been matched with a canceled SPIRE position: %s""";

            public static final String MATCHED_POSITION_LOAN_CONTRACT_PROPOSAL_MSG = """
                The loan contract proposal %s has been matched \
                with a SPIRE position %s and needs now to be validated.""";

            public static final String POSITION_CANCELED_MSG = """
                The position %s has been canceled""";

            public static final String POSITION_CANCELED_SUBMITTED_MSG = """
                The position %s has been canceled and the cancellation of \
                the 1Source loan contract proposal %s generated \
                from this position has been instructed to 1Source""";

            public static final String POSITION_SUBMITTED_MSG = """
                A loan contract proposal has been generated from the position %s and posted to 1Source""";

            public static final String POSITION_UNMATCHED_MSG = """
                A new position %s has been created \
                but not yet matched with a loan contract proposal.""";

            public static final String POSITION_UPDATE_SUBMITTED_MSG = """
                A loan contract proposal has been generated from the updated position %s and posted to 1Source""";

            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_APPROVED_MSG = """
                The loan contract proposal %s has been approved in 1Source but the loan contract proposal \
                has not been retrieved in the Integration toolkit.""";

            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_CANCELED_MSG = """
                The loan contract proposal %s has been canceled in 1Source but the loan contract proposal \
                has not been retrieved in the Integration toolkit.""";

            public static final String TOOLKIT_ISSUE_GET_LOAN_CONTRACT_DECLINED_MSG = """
                The loan contract proposal %s has been declined in 1Source but the loan contract proposal \
                has not been retrieved in the Integration toolkit.""";

            public static final String TOOLKIT_ISSUE_PROCESS_TRADE_CANCELATION_MSG = """
                The trade agreement %s has been canceled in 1Source but the trade agreement \
                has not been retrieved in the Integration toolkit""";

            public static final String TRADE_AGREEMENT_CREATE_EVENT_MSG = """
                A new trade agreement %s has been captured""";

            public static final String TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG = """
                The trade agreement %s matched with the position %s has been canceled""";

            public static final String TRADE_AGREEMENT_CANCELED_EVENT_MSG = """
                The trade agreement %s related to the shared trade ticket %s has been canceled""";

            public static final String TRADE_AGREEMENT_UNMATCHED_MSG = """
                A new trade agreement %s has been captured but not yet matched with a position""";

            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG = """
                The Lender's loan contract proposal, contractId %s, is matching the canceled positionId %s""";

        }
    }

    public static class ContractSettlement {

        public static class Subject {

            public static final String CAPTURE_POSITION_SETTLEMENT_EXCEPTION_SPIRE = "Capture of settled positions - %s";
            public static final String GET_POSITION_SETTLEMENT_EXCEPTION_SPIRE = "Position - %s";
            public static final String LOAN_CONTRACT_SETTLED = "Position - %s";
            public static final String POSITION_SETTLED_SUBMITTED_SUBJECT = "Position - %s";
            public static final String PROCESS_POSITION_SETTLED_SUBJECT = "Position - %s";
            public static final String POST_LOAN_CONTRACT_UPDATE_EXCEPTION = "Position - %s";
        }

        public static class DataMsg {

            public static final String CAPTURE_POSITION_SETTLEMENT_EXCEPTION_MSG = """
                Settled positions cannot be captured from SPIRE for the following reason: %s""";

            public static final String GET_LOAN_CONTRACT_SETTLED_ISSUE_MSG = """
                The loan contract pending settlement %s has been marked as settled in 1Source \
                but the loan contract pending settlement has not been retrieved in the Integration toolkit.""";

            public static final String LOAN_CONTRACT_SETTLED_MSG = """
                The 1Source loan contract %s related to the position %s has been updated as settled.""";

            public static final String POSITION_SETTLED_SUBMITTED_MSG = """
                The position %s related to 1Source loan contract %s is settled.""";

            public static final String POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG = """
                The settlement status of the loan contract: %s (SPIRE position: %s) \
                cannot be updated for the following reason: %s""";
        }
    }

    public static class Recall {

        public static class Subject {

            public static final String PROCESS_SPIRE_RECALL_INSTR_SUBJECT = "SPIRE Recall - %s";
            public static final String RECALL_SUBMITTED_SUBJECT = "SPIRE Recall id %s - PositionId %s";
        }

        public static class DataMsg {

            public static final String PROCESS_SPIRE_RECALL_INSTR_MSG = "The SPIRE recall %s "
                + "has not been processed by 1Source for the following reason: %s";

            public static final String RECALL_SUBMITTED_MSG = "The recall %s has been submitted "
                + "to 1Source to request the recall creation";
        }
    }

    public static class Rerate {

        public static class Subject {

            public static final String POST_RERATE_EXCEPTION_1SOURCE = "Trade - %s";
            public static final String GET_RERATE_EXCEPTION_1SOURCE = "1Source Rerate - %s";
            public static final String MATCHED_RERATE = "Trade - %s";
            public static final String CREATED_RERATE = "Trade - %s";
            public static final String UNMATCHED_RERATE = "1Source rerate id - %s";
            public static final String APPROVED_RERATE = "Trade - %s";
            public static final String APPLIED_RERATE = "Trade - %s";
            public static final String DECLIED_RERATE = "1Source Rerate - %s";
            public static final String CANCELED_RERATE_PROPOSAL = "1Source Rerate - %s";
            public static final String RERATE_CANCELED = "1Source Rerate - %s";
            public static final String RERATE_CANCEL_PENDING = "1Source Rerate - %s";
            public static final String REPLACED_RERATE_TRADE = "Trade - %s";
            public static final String CANCEL_RERATE = "Trade - %s";
            public static final String CANCELED_RERATE = "Trade - %s";
            public static final String APPROVE_EXCEPTION_RERATE = "Trade - %s";
            public static final String APPROVE_TECHNICAL_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String APPLIED_TECHNICAL_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String DECLINE_TECHNICAL_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String DECLINE_NOT_AUTHORIZED_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String CANCELED_TECHNICAL_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE = "1Source Rerate - %s";
            public static final String CONFIRM_EXCEPTION_RERATE = "Trade - %s";
            public static final String DECLINE_EXCEPTION_RERATE = "Trade - %s";
            public static final String CANCEL_EXCEPTION_RERATE = "Trade - %s";
            public static final String REPLACE_EXCEPTION_RERATE = "Trade - %s";
        }

        public static class DataMsg {

            public static final String POST_RERATE_EXCEPTION_1SOURCE_MSG = "The rerate proposal instruction (generated from the SPIRE rerate trade: %s) has not been processed by 1Source for the following reason: %s";
            public static final String GET_RERATE_EXCEPTION_1SOURCE_MSG = "The details of rerate proposal %s have not been retrieved from 1Source for the following reason: %s";
            public static final String MATCHED_RERATE_MSG = "The rerate proposal %s has been matched with a SPIRE rerate trade: %s and needs now to be validated.";
            public static final String MATCHED_FOR_APPROVE_RERATE_MSG = "The rerate proposal %s has been created in 1Source from the SPIRE rerate trade: %s  and needs now to be approved by the counter-party.";
            public static final String CREATED_RERATE_MSG = "A new rerate trade %s has been captured but yet matched with a rerate proposal.";
            public static final String UNMATCHED_RERATE_MSG = "The rerate proposal %s has been captured but not yet matched with a SPIRE trade event";
            public static final String APPROVED_RERATE_MSG = "The rerate proposal %s matching with the SPIRE rerate trade %s has been approved";
            public static final String APPLIED_RERATE_MSG = "The rerate %s matching with the SPIRE rerate trade %s has been now applied on the loan contract %s";
            public static final String DECLIED_RERATE_MSG = "The rerate proposal %s has been declined.";
            public static final String CANCELED_RERATE_PROPOSAL_MSG = "The rerate proposal %s has been canceled.";
            public static final String RERATE_CANCELED_MSG = "The rerate %s has been canceled.";
            public static final String RERATE_CANCEL_PENDING_MSG = "The rerate %s has been cancel pending.";
            public static final String REPLACED_RERATE_TRADE_MSG = "The SPIRE rerate trade %s has been replaced with a SPIRE rerate trade: %s .";
            public static final String CANCEL_RERATE_MSG = "The update of the SPIRE rerate trade %s replaced with the SPIRE rerate trade: %s has led to the cancelation of the rerate proposal %s.";
            public static final String CANCELED_RERATE_MSG = "The update of the SPIRE rerate trade %s offset by the SPIRE rerate trade: %s has led to the cancelation of the rerate proposal %s .";
            public static final String APPROVE_EXCEPTION_RERATE_MSG = "The rerate proposal %s matching with the rerate trade %s cannot be approved for the following reason: %s";
            public static final String APPROVE_TECHNICAL_EXCEPTION_RERATE_MSG = "The rerate proposal %s has been approved but the rerate proposal has not been retrieved in the Integration toolkit";
            public static final String APPLIED_TECHNICAL_EXCEPTION_RERATE_MSG = "The rerate %s has been applied in 1Source but the rerate has not been retrieved in the Integration toolkit";
            public static final String DECLINE_TECHNICAL_EXCEPTION_RERATE_MSG = "The rerate proposal % has been declined in 1Source but the rerate proposal has not been retrieved in the Integration toolkit.";
            public static final String CANCELED_TECHNICAL_EXCEPTION_RERATE_MSG = "The rerate proposal % has been canceled in 1Source but the rerate proposal has not been retrieved in the Integration toolkit.";
            public static final String CANCEL_PENDING_TECHNICAL_EXCEPTION_RERATE_MSG = "The rerate pending settlement %s has been canceled by one of the two counterparties in 1Source but the rerate pending settlement has not been retrieved in the Integration toolkit.";
            public static final String REPLACE_RERATE_EXCEPTION_RERATE_MSG = "The rerate trade %s has not been retrieved in the Integration toolkit";
            public static final String DECLINE_RERATE_EXCEPTION_RERATE_MSG = "The rerate %s has not been retrieved in the Integration toolkit";
            public static final String CONFIRM_EXCEPTION_RERATE_MSG = "The rerate trade %s have not been confirmed in SPIRE. The identifier of the confirmed 1Source rerate is : %s. The reason is: %s";
            public static final String DECLINE_EXCEPTION_RERATE_MSG = "The rerate proposal %s matching with the rerate trade %s cannot be declined for the following reason: %s";
            public static final String DECLINE_NOT_AUTHORIZED_EXCEPTION_RERATE_MSG = "The decline instruction for the 1Source rerate %s is not authorised as not generated from a “RERATE_PROPOSAL_DISCREPANCIES” or “RERATE_PROPOSAL_UNMATCHED” event.";
            public static final String CANCEL_EXCEPTION_RERATE_MSG = "The rerate proposal %s matching with the rerate trade %s cannot be canceled for the following reason: %s";

        }
    }

    public static class Return {

        public static class Subject {

            public static final String GET_NEW_RETURN_PENDING_CONFIRMATION_TE_SBJ = "Extract of new return pending conf. - %s";
            public static final String POST_RETURN_PENDING_CONFIRMATION_TE_SBJ = "Trade - %s";
            public static final String POST_RETURN_SUBMITTED_SBJ = "Trade - %s";
        }

        public static class DataMsg {

            public static final String GET_NEW_RETURN_PENDING_CONFIRMATION_TE_MSG = "New return pending confirmation cannot be extracted from SPIRE for the following reason: %s";
            public static final String POST_RETURN_PENDING_CONFIRMATION_TE_MSG = "The return instruction (generated from the SPIRE return trade: %s) has not been processed by 1Source for the following reason: %s";
            public static final String POST_RETURN_SUBMITTED_MSG = "The return trade %s has been submitted to 1Source to request the return creation";
        }
    }

    public static class Generic {

        public static class Subject {

            public static final String GET_EVENTS_EXCEPTION_1SOURCE = "1Source event capture - %s";
            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_APPROVED = "Loan contract proposal - %s, "
                + "or Position - %s";
            public static final String GET_EVENTS_TRADE_AGREEMENT_CANCELED = "1Source trade agreement - %s, or Position - %s";
            public static final String GET_EVENTS_TRADE_AGREEMENT_CREATED = "1Source trade agreement - %s, or Position - %s";
            public static final String GET_TRADE_EVENTS_PENDING_CONFIRMATION = "SPIRE trade event capture - %s";
        }

        public static class DataMsg {

            public static final String GET_EVENTS_EXCEPTION_1SOURCE_MSG = "Events cannot be retrieved from 1Source "
                + "for the following reason: %s";
            public static final String GET_TRADE_EVENTS_PENDING_CONFIRMATION_MSG = "SPIRE TRADE Events cannot be retrieved from SPIRE for the following reason: %s";
        }
    }

    public static class MaintainOnesourceParticipantsList {

        public static class Subject {

            public static final String GET_PARTICIPANTS_EXCEPTION_1SOURCE = "Daily_1Source_Participants_list_extract - %s";
            public static final String POST_PARTICIPANT_EXCEPTION_SPIRE = "Parcitipant LEI - %s";
        }

        public static class DataMsg {

            public static final String GET_PARTICIPANTS_1SOURCE_MSG = """
                The 1Source participant list cannot be retrieved from 1Source for the following reason: %s""";
        }
    }
}
