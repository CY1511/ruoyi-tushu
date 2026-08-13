package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Address;

@Mapper
public interface AddressMapper {
    List<Address> selectAddressList(Long userId);
    Address selectAddressById(Long id);
    Address selectDefaultAddress(Long userId);
    int insertAddress(Address address);
    int updateAddress(Address address);
    int deleteAddressById(Long id);
    int clearDefault(Long userId);
}