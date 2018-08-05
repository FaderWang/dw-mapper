package com.faderw.mapper;

import com.faderw.annotation.Mapper;
import com.faderw.dbmodel.Country;
import tk.mybatis.mapper.common.sqlserver.InsertMapper;

/**
 * @author FaderW
 */
@Mapper("ceres")
public interface CountryMapper extends InsertMapper<Country>{
}
