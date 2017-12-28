package com.zydcompany.management.config.mybatis.sharding;


import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.Data;

import java.util.Collection;

public class CustomDataSourcePreciseShardingAlgorithm implements PreciseShardingAlgorithm {
    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        return availableTargetNames.parallelStream()
                .filter(dataSourceName -> dataSourceName.toString().endsWith(ShardingHelper.getSharding((String) shardingValue.getValue(), 2)))
                .findFirst()
                .toString();

    }

}
