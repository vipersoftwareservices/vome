/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2016/06/15
 *
 * Copyright 1998-2016 by Viper Software Services
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Viper Software Services. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Viper Software Services.
 *
 * @author Tom Nevin (TomNevin@pacbell.net)
 *
 * @version 1.0, 06/15/2016 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.test;

import java.util.List;
import java.util.Stack;

import org.junit.Test;

import com.viper.vome.bean.Grammar;
import com.viper.vome.bean.Literal;
import com.viper.vome.bean.Rule;
import com.viper.vome.beans.JAXBUtils;
import com.viper.vome.grammar.GrammarCompletion;
import com.viper.vome.util.FileUtil;

public class TestSqlGrammar extends AbstractTestCase {

    @Test
	public void testJAXBGrammar() throws Exception {
		String str = FileUtil.readFile("etc/grammar.xml");
		Grammar grammar = JAXBUtils.getObject(Grammar.class, str);

		assertNotNull("Grammer should not be null", grammar);
		assertEquals("Grammar should have correct number of rules", 58, grammar.getRule().size());
	}

    @Test
	public void testTokenizer() throws Exception {
		GrammarCompletion parser = new GrammarCompletion("etc/grammar.xml");
		List<String> tokens = parser.toTokens("select * from table");

		assertNotNull("Tokens should not be null", tokens);
		assertEquals("Numer of tokens is incorrect", 4, tokens.size());
		assertEquals("tokens[0]", "select", tokens.get(0));
		assertEquals("tokens[1]", "*", tokens.get(1));
		assertEquals("tokens[2]", "from", tokens.get(2));
		assertEquals("tokens[3]", "table", tokens.get(3));
	}

    @Test
	public void testGrammerBasic() throws Exception {
		System.out.println("*** testGrammerBasic: ");
		
		GrammarCompletion parser = new GrammarCompletion("etc/grammar.xml");
		Stack<Object> stack = parser.parseGrammar("select * from table");

		for (Object item : stack) {
			System.out.println("Stack Item: " + item.toString());
		}
		
		assertNotNull("Stack should not be null", stack);
		assertEquals("stack is not correct size", 5, stack.size());
	}

    @Test
	public void testGrammerSelectBasic() throws Exception {
		System.out.println("*** testGrammerSelectBasic: ");
		
		GrammarCompletion parser = new GrammarCompletion("etc/grammar.xml");
		Stack<Object> stack = parser.parseGrammar("select * from");

		assertNotNull("Stack should not be null", stack);
		assertEquals("stack is not correct size", 4, stack.size());
	}

    @Test
	public void testGrammerBrokenAlterDomain() throws Exception {
		System.out.println("*** testGrammerBrokenAlterDomain: ");
		
		GrammarCompletion parser = new GrammarCompletion("etc/grammar.xml");
		Stack<Object> stack = parser.parseGrammar("alter domain");
	
		for (Object item : stack) {
			System.out.println("Stack Item: " + item.toString());
		}
		
		assertNotNull("Stack shoul not be null", stack);
		assertEquals("stack is not correct size", 3, stack.size());
	}

    @Test
	public void testGrammerSelectExpr() throws Exception {
		System.out.println("*** testGrammerSelectExpr: ");
		
		GrammarCompletion parser = new GrammarCompletion("etc/grammar.xml");
		Stack<Object> stack = parser.parseGrammar("*");
		
		assertNotNull("Stack shoul not be null", stack);
		assertEquals("stack is not correct size", 2, stack.size());
		assertEquals("Rule is not correct", "select-expr", ((Rule) stack.pop()).getName());
		assertEquals("Literal is not correct", "*", ((Literal) stack.pop()).getValue());
	}
}