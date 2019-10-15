package com.itdr.services;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Shipping;

public interface ShippingService {
    ServerResponse All(Integer id);

    ServerResponse addShipping(Integer id, Shipping shipping);

    ServerResponse updateShipping(Integer uid, Shipping shipping);

    ServerResponse deleteShipping(Integer id, Integer id1);
}
