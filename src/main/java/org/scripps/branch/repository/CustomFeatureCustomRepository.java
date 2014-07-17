package org.scripps.branch.repository;

import org.scripps.branch.entity.CustomFeature;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFeatureCustomRepository {

	CustomFeature getByPostfixExpr(String postExp);

}
