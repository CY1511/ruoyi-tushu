package com.ruoyi.system.service;

import com.ruoyi.system.domain.Address;

import java.util.List;

public interface IAddressService {

    List<Address> selectAddressList(Long userId);

    Address selectAddressById(Long id);

    Address selectDefaultAddress(Long userId);

    int insertAddress(Address address);

    int updateAddress(Address address);

    int deleteAddressById(Long id);

    int setDefault(Long userId, Long addressId);
}