package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Address;
import com.ruoyi.system.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @GetMapping("/list")
    public R getAddressList(@RequestParam Long userId) {
        List<Address> addresses = addressService.selectAddressList(userId);
        return R.ok(addresses);
    }

    @GetMapping("/{id}")
    public R getAddressById(@PathVariable Long id) {
        Address address = addressService.selectAddressById(id);
        return R.ok(address);
    }

    @GetMapping("/default")
    public R getDefaultAddress(@RequestParam Long userId) {
        Address address = addressService.selectDefaultAddress(userId);
        return R.ok(address);
    }

    @PostMapping("/add")
    public R addAddress(@RequestBody Address address) {
        addressService.insertAddress(address);
        return R.ok("添加成功");
    }

    @PutMapping("/update")
    public R updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteAddress(@PathVariable Long id) {
        addressService.deleteAddressById(id);
        return R.ok("删除成功");
    }

    @PutMapping("/default")
    public R setDefault(@RequestBody Address address) {
        addressService.setDefault(address.getUserId(), address.getId());
        return R.ok("设置默认地址成功");
    }
}