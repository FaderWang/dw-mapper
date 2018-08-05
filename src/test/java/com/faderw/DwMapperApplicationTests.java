package com.faderw;

import com.faderw.dbmodel.Country;
import com.faderw.mapper.CountryMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DwMapperApplicationTests {

	@Autowired
	CountryMapper countryMapper;

	@Test
	public void contextLoads() {
		Country country = new Country();
		country.setName("China");
		int effectNums = countryMapper.insert(country);
		Assert.assertEquals(1, effectNums);
	}

}
