package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.CustomFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import weka.core.AttributeExpression;

@Repository
public class CustomFeatureCustomRepositoryImpl implements CustomFeatureCustomRepository {
	
	@Autowired
	CustomFeatureRepository cfeatureRepo;
	
	@Override
	public CustomFeature getByPostfixExpr(String postExp) {
		List<CustomFeature> cfList = cfeatureRepo.findAll();
		AttributeExpression _attr = new AttributeExpression();
		String postfixExp = "";
		for(CustomFeature cf : cfList){
			postfixExp = cf.getExpression();
			try {
				_attr.convertInfixToPostfix(postfixExp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(postExp.equals(postfixExp)){
				return cf;
			}
		}
		return null;
	}

}
