package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.Address;
import com.ruoyi.system.mapper.AddressMapper;
import com.ruoyi.system.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> selectAddressList(Long userId) {
        return addressMapper.selectAddressList(userId);
    }

    @Override
    public Address selectAddressById(Long id) {
        return addressMapper.selectAddressById(id);
    }

    @Override
    public Address selectDefaultAddress(Long userId) {
        return addressMapper.selectDefaultAddress(userId);
    }

    @Override
    @Transactional
    public int insertAddress(Address address) {
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefault(address.getUserId());
        }
        address.setStatus(1);
        return addressMapper.insertAddress(address);
    }

    @Override
    @Transactional
    public int updateAddress(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(address.getUserId());
        }
        return addressMapper.updateAddress(address);
    }

    @Override
    @Transactional
    public int deleteAddressById(Long id) {
        return addressMapper.deleteAddressById(id);
    }

    @Override
    @Transactional
    public int setDefault(Long userId, Long addressId) {
        addressMapper.clearDefault(userId);
        Address address = new Address();
        address.setId(addressId);
        address.setIsDefault(1);
        return addressMapper.updateAddress(address);
    }
}