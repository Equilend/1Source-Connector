package com.intellecteu.onesource.integration.mapper;

import com.intellecteu.onesource.integration.model.Rate;
import com.intellecteu.onesource.integration.model.RebateRate;
import com.intellecteu.onesource.integration.model.Rerate;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FeeRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FloatingRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRebateRateRebateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.OneOfRerateRerateDTODTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class RerateMapper {

    public abstract Rerate toModel(RerateDTO rerateDTO);

    public Rate map(OneOfRerateRateDTODTO rateDTO) {
        if (rateDTO instanceof RebateRateDTO) {
            return mapRebate((RebateRateDTO) rateDTO);
        } else if (rateDTO instanceof FeeRateDTO) {
            return mapFee((FeeRateDTO) rateDTO);
        }
        return null;
    }

    public Rate map(OneOfRerateRerateDTODTO rerateDTO) {
        if (rerateDTO instanceof RebateRateDTO) {
            return mapRebate((RebateRateDTO) rerateDTO);
        } else if (rerateDTO instanceof FeeRateDTO) {
            return mapFee((FeeRateDTO) rerateDTO);
        }
        return null;
    }

    public abstract Rate mapRebate(RebateRateDTO rebateRateDTO);

    public abstract Rate mapFee(FeeRateDTO rebateRateDTO);

    public RebateRate map(OneOfRebateRateRebateDTODTO rebateDTO) {
        if (rebateDTO instanceof FixedRateDTO) {
            return mapFixedRate((FixedRateDTO) rebateDTO);
        } else if (rebateDTO instanceof FloatingRateDTO) {
            return mapFloatingRate((FloatingRateDTO) rebateDTO);
        }
        return null;
    }

    public abstract RebateRate mapFixedRate(FixedRateDTO rebateDTO);

    public abstract RebateRate mapFloatingRate(FloatingRateDTO rebateDTO);

}
