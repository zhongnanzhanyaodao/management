package com.zydcompany.management.config.mybatis.sharding;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

public class CustomTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm {
    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        return availableTargetNames.parallelStream()
                .filter(tableName -> tableName.toString().endsWith(ShardingHelper.getSharding((String) shardingValue.getValue(), 10)))
                .findFirst()
                .toString();

    }

}