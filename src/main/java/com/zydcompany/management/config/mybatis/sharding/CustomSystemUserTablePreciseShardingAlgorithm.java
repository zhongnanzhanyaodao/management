package com.zydcompany.management.config.mybatis.sharding;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

public class CustomSystemUserTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<String> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(ShardingHelper.getSharding(shardingValue.getValue(), ShardingConstant.SYSTEM_USER_SHARDING_COUNT))) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

    /*@Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        return availableTargetNames.parallelStream()
                .filter(dataSourceName -> dataSourceName.endsWith(ShardingHelper.getSharding(shardingValue.getValue(), ShardingConstant.SYSTEM_USER_SHARDING_COUNT)))
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new);
    }*/

}