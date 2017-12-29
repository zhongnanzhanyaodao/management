package com.zydcompany.management.config.mybatis.sharding;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

public class CustomUserDetailSupplementTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<BigDecimal> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(ShardingHelper.getSharding(shardingValue.getValue().toString(), ShardingConstant.USER_DETAIL_SUPPLEMENT_COUNT))) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}
