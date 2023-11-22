package com.intellecteu.onesource.integration.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordMessageConstant {

  public static final String NOT_APPLICABLE = "N/A";

  public static class ContractInitiation {

    public static class Subject {

      public static final String GET_AGREEMENT_EXCEPTION_1SOURCE = "1Source event id - %s";
      public static final String GET_POSITION_CONFIRMATION_EXCEPTION_SPIRE = """
          Extract of new positions pending conf. - %s""";
      public static final String GET_SETTLEMENT_INSTR_EXCEPTION_SPIRE = "Position - %s";
      public static final String APPROVE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String CANCEL_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String DECLINE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_1SOURCE = "Position - %s";
      public static final String POST_POSITION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
      public static final String POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_SPIRE = "Position - %s";
      public static final String TRADE_AGREEMENT_DISCREPANCIES = "Position - %s";
      public static final String TRADE_AGREEMENT_RECONCILED = "Position - %s";
      public static final String TRADE_AGREEMENT_CREATED = "1Source trade agreement - %s";
      public static final String TRADE_AGREEMENT_MATCHED_POSITION = "Position - %s";
      public static final String TRADE_AGREEMENT_MATCHED_CANCELED_POSITION = "Position - %s";
      public static final String LOAN_CONTRACT_MATCHED_POSITION = "Position - %s";
      public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_EXCEPTION_1SOURCE = "Position - %s";
      public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION = "Position - %s";
      public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED = "Position - %s";
    }

    public static class DataMsg {

      public static final String APPROVE_LOAN_PROPOSAL_EXCEPTION_MSG = """
          The loan contract %s cannot be approved for the following reason: %s""";

      public static final String CANCEL_LOAN_PROPOSAL_MSG = """
          The loan contract %s cannot be canceled for the following reason: %s""";

      public static final String DECLINE_LOAN_PROPOSAL_EXCEPTION_MSG = """
          The loan contract %s cannot be declined for the following reason: %s""";

      public static final String GET_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG = """
          The details of loan contract proposal %s have not been retrieved \
          from 1Source for the following reason: %s""";

      public static final String GET_POSITION_EXCEPTION_MSG = """
          New positions pending confirmation cannot be extracted from SPIRE for the following reason: %s""";

      public static final String GET_SETTLEMENT_INSTRUCTIONS_EXCEPTION_MSG = """
          The settlement instruction applicable to the trade %s (SPIRE position: %s ) \
          cannot be retrieved from SPIRE for the following reason: %s""";

      public static final String GET_TRADE_AGREEMENT_EXCEPTION_1SOURCE_MSG = """
          “The details of the trade agreement %s cannot be retrieved \
          from 1Source for the following reason: %s""";

      public static final String POST_LOAN_CONTRACT_PROPOSAL_EXCEPTION_MSG = """
          The loan contract proposal instruction has not been processed by 1Source \
          for the SPIRE Position: %s for the following reason: %s""";

      public static final String POST_LOAN_CONTRACT_PROPOSAL_UPDATE_EXCEPTION_MSG = """
          The loan contract %s cannot be updated for the following reason: %s""";

      public static final String POST_POSITION_UPDATE_EXCEPTION_MSG = """
          The position %s have not been updated in SPIRE with the loan contract \
          identifier: %s for the following reason: %s""";

      public static final String POST_SETTLEMENT_INSTRUCTION_UPDATE_EXCEPTION_MSG = """
          The counterparty settlement instruction for the loan contract %s \
          (SPIRE position: %s) has not been recorded in SPIRE for the following reason: %s""";

      public static final String RECONCILE_TRADE_AGREEMENT_FAIL_MSG = """
          The trade agreement %s is in discrepancies with the position %s in SPIRE.
          List of discrepancies:
          %s""";

      public static final String RECONCILE_TRADE_AGREEMENT_SUCCESS_MSG = """
          The trade agreement %s has been successfully reconciled \
          with the position %s in SPIRE""";

      public static final String MATCHED_POSITION_TRADE_AGREEMENT_MSG = """
          The trade agreement %s has been matched \
          with a SPIRE position %s""";

      public static final String MATCHED_CANCELED_POSITION_TRADE_AGREEMENT_MSG = """
          The trade agreement %s has been matched with a canceled SPIRE position: %s""";

      public static final String MATCHED_POSITION_LOAN_CONTRACT_PROPOSAL_MSG = """
          The loan contract proposal %s has been matched \
          with a SPIRE position %s""";


      public static final String TRADE_AGREEMENT_CREATE_EVENT_MSG = """
          A new trade agreement %s has been captured""";

      public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_CANCELED_POSITION_MSG = """
          The Lender's loan contract proposal, contractId %s is matching a canceled position, positionId %s""";

      public static final String VALIDATE_LOAN_CONTRACT_PROPOSAL_VALIDATED_MSG = """
          The Lender’s loan contract proposal %s has been successfully reconciled \
          with the position %s and related settlement instructions""";

    }
  }

  public static class ContractSettlement {

    public static final String GET_POSITION_SETTLEMENT_EXCEPTION_SPIRE = "Position - %s";
    public static final String POST_LOAN_CONTRACT_UPDATE_EXCEPTION_SPIRE = "Position - %s";
  }

  public static class Generic {

    public static class EventTypeMessage {

      public static final String CONTRACT_APPROVE_MSG = """
          The loan contract proposal (contract identifier: %s) matching with the \
          SPIRE position (position identifier: %s) has been approved by the Borrower""";

      public static final String CONTRACT_CANCEL_MSG = """
          The loan contract proposal (contract identifier: %s) matching with the \
          SPIRE position (position identifier: %s) has been canceled by the Lender \
          for the following reasons:\s""";
      public static final String CONTRACT_CREATE_MSG = """
          The loan contract proposal (contract identifier: %s) matching with the \
           SPIRE position (position identifier: %s) has been created by the Lender""";
      public static final String CONTRACT_DECLINE_MSG = """
          The loan contract proposal (contract identifier: %s) matching with the \
          SPIRE position (position identifier: %s) has been declined by the Borrower \
          for the following reason.""";
    }

    public static class Subject {

      public static final String GET_EVENTS_EXCEPTION_1SOURCE = "1Source event capture - %s";
      public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_APPROVED = "Loan contract proposal - %s, "
          + "or Position - %s";
      public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CANCELED = "Loan contract proposal - %s, "
          + "or Position - %s";
      public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_CREATED = "Loan contract proposal - %s, "
          + "or Position - %s";
      public static final String GET_EVENTS_LOAN_CONTRACT_PROPOSAL_DECLINED = "Loan contract proposal - %s, "
          + "or Position - %s";
      public static final String GET_EVENTS_TRADE_AGREEMENT_CANCELED = "1Source trade agreement - %s, or Position - %s";
      public static final String GET_EVENTS_TRADE_AGREEMENT_CREATED = "1Source trade agreement - %s, or Position - %s";
    }

    public static class DataMsg {

      public static final String GET_EVENTS_EXCEPTION_1SOURCE_MSG = "Events cannot be retrieved from 1Source "
          + "for the following reason: %s";
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
