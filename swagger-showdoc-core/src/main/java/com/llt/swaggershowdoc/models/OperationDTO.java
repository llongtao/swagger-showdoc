package com.llt.swaggershowdoc.models;

import io.swagger.models.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LILONGTAO
 * @date 2020-09-30
 */
@Getter
@AllArgsConstructor
public class OperationDTO {

    private final Operation operation;
    private final String method;
}
