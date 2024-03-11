package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordMessageConstant {

    public static final String NOT_APPLICABLE = "N/A";

    public static class ContractInitiation {

        public static class Subject {

            public static final String APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String GET_AGREEMENT_EXCEPTION_1SOURCE = "1Source event id - %s";
            public static final String GET_COUNTERPARTY_SETTLEMENT_INSTRUCTION_SUBJECT = "Position - %s";

            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED = "Loan contract proposal - %s";
            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED = "Position - %s";
            public static final String GET_LOAN_CONTRACT_PROPOSAL_DISCREPANCIES = "Position - %s";
            public static final String GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE = """
                Extract of new positions pending conf. - %s""";
            public static final String GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE = "Position - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_APPROVED = "Position - %s";
            public static final String GET_UPDATED_POSITIONS_PENDING_CONFIRMATION_EXCEPTION_SPIRE = """
                Extract of new positions pending conf. - %s""";
            public static final String CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Loan contract proposal - %s";
            public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED = "Loan contract proposal - %s";
            public static final String LOAN_CONTRACT_PROPOSAL_UNMATCHED_SUBJECT = "Loan contract proposal - %s";
            public static final String POSITION_CANCELED_SUBJECT = "Position - %s";
            public static final String POSITION_CANCELED_SUBMITTED_SUBJECT = "Position - %s";
            public static final String POSITION_UNMATCHED_SUBJECT = "Position - %s";
            public static final String POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE = "Position - %s";
            public static final String POST_POSITION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
            public static final String POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
            public static final String TRADE_AGREEMENT_DISCREPANCIES = "Position - %s";
            public static final String TRADE_AGREEMENT_RECONCILED = "Position - %s";
            public static final String TRADE_AGREEMENT_CREATED = "1Source trade agreement - %s";
            public static final String TRADE_AGREEMENT_CANCELED = "1Source trade agreement - %s";
            public static final String TRADE_AGREEMENT_CANCELED_MATCHED_POSITION = "Position - %s";
            public static final String TRADE_AGREEMENT_MATCHED_POSITION = "Position - %s";
            public static final String TRADE_AGREEMENT_MATCHED_CANCELED_POSITION = "Position - %s";
            public static final String LOAN_CONTRACT_MATCHED_POSITION = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION = "Position - %s";
            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED = "Position - %s";
        }

        public static class DataMsg {

            public static final String APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG = """
                The loan contract %s matching with the position %s cannot be approved for the following reason: %s""";

            public static final String CANCEL_LOAN_PROPOSAL_MSG = """
                The loan contract proposal %s (generated from the SPIRE Position: %s) \
                cannot be canceled for the following reason: %s""";

            public static final String CONTRACT_CANCEL_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been canceled by the Lender \
                for the following reasons: %s""";

            public static final String CONTRACT_CREATE_MSG = """
                The loan contract proposal %s has been created but not yet matched with a SPIRE position""";

            public static final String CONTRACT_DECLINE_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been declined by the Borrower \
                for the following reason.""";

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

            public static final String LOAN_CONTRACT_PROPOSAL_APPROVE_MSG = """
                The loan contract proposal %s matching with the \
                SPIRE position %s has been approved by the Borrower""";

            public static final String LOAN_CONTRACT_PROPOSAL_RECONCILIATION_MSG = """
                The Lender’s loan contract proposal %s has been successfully reconciled \
                with the position %s""";

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
                and the matched SPIRE position %s. \n
                List of discrepancies:
                %s""";

            public static final String RECONCILE_TRADE_AGREEMENT_DISCREPANCIES_MSG = """
                The trade agreement %s is in discrepancies with the position %s in SPIRE.
                List of discrepancies:
                %s""";

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
                with a SPIRE position %s""";

            public static final String POSITION_CANCELED_MSG = """
                The position %s has been canceled""";

            public static final String POSITION_CANCELED_SUBMITTED_MSG = """
                “The position %s has been canceled and the cancellation of \
                the 1Source loan contract proposal %s generated \
                from this position has been instructed to 1Source""";

            public static final String POSITION_UNMATCHED_MSG = """
                A new position %s has been created \
                but not yet matched with a loan contract proposal.""";

            public static final String TRADE_AGREEMENT_CREATE_EVENT_MSG = """
                A new trade agreement %s has been captured""";

            public static final String TRADE_AGREEMENT_MATCHED_CANCELED_EVENT_MSG = """
                The trade agreement %s matched with the position %s has been canceled""";

            public static final String TRADE_AGREEMENT_CANCELED_EVENT_MSG = """
                The trade agreement %s has been canceled""";

            public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG = """
                The Lender's loan contract proposal, contractId %s, is matching the canceled positionId %s""";

        }
    }

    public static class ContractSettlement {

        public static class Subject {

            public static final String LOAN_CONTRACT_SETTLED = "Position - %s";

            public static final String GET_POSITION_SETTLEMENT_EXCEPTION_SPIRE = "Position - %s";
            public static final String POST_LOAN_CONTRACT_UPDATE_EXCEPTION = "Position - %s";
        }

        public static class DataMsg {

            public static final String LOAN_CONTRACT_SETTLED_MSG = """
                The 1Source loan contract: %s has been updated as settled.""";

            public static final String POST_LOAN_CONTRACT_UPDATE_EXCEPTION_MSG = """
                The settlement status of the loan contract: %s (SPIRE position: %s) \
                cannot be updated for the following reason: %s""";
        }
    }

    public static class Rerate {

        public static class Subject {

            public static final String GET_RERATE_EXCEPTION_1SOURCE = "1Source Rerate - %s";
            public static final String MATCHED_RERATE = "Trade - %s";
            public static final String CREATED_RERATE = "Trade - %s";
            public static final String UNMATCHED_RERATE = "1Source rerate id -  %s";
        }

        public static class DataMsg {

            public static final String GET_RERATE_EXCEPTION_1SOURCE_MSG = "The details of rerate proposal %s have not been retrieved from 1Source for the following reason: %s";
            public static final String MATCHED_RERATE_MSG = "The rerate proposal %s has been matched with a SPIRE rerate trade: %s and needs now to be validated.";
            public static final String CREATED_RERATE_MSG = "A new rerate trade %s has been captured but yet matched with a rerate proposal.";
            public static final String UNMATCHED_RERATE_MSG = "The rerate proposal %s has been captured but not yet matched with a SPIRE trade event";
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
