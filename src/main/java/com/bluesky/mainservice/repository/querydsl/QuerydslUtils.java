package com.bluesky.mainservice.repository.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;

public class QuerydslUtils {
    public static BooleanExpression orBuilder(BooleanExpression... expression) {
        BooleanExpression expressionChain = null;
        for (BooleanExpression e : expression) {
            if (e != null && expressionChain == null) {
                expressionChain = e;
                continue;
            }
            if (e != null) {
                expressionChain = expressionChain.or(e);
            }
        }
        return expressionChain;
    }

    public static OrderSpecifier orderByBuilder(OrderSpecifier... orderSpecifier) {
        for (OrderSpecifier o : orderSpecifier) {
            if (o != null) {
                return o;
            }
        }
        throw new IllegalArgumentException("파라미터의 값이 전부 null 입니다.");
    }
}
