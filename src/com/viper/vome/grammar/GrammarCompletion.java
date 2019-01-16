/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2003/06/15
 *
 * Copyright 1998-2003 by Viper Software Services
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
 * @version 1.0, 06/15/2003 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBElement;

import com.viper.vome.bean.Choice;
import com.viper.vome.bean.Grammar;
import com.viper.vome.bean.Item;
import com.viper.vome.bean.Literal;
import com.viper.vome.bean.Rule;
import com.viper.vome.bean.Term;
import com.viper.vome.bean.UseRule;
import com.viper.vome.beans.JAXBUtils;
import com.viper.vome.util.FileUtil;

import javafx.scene.control.ContextMenu;

public class GrammarCompletion {

	private static final JAXBUtils jaxbUtils = JAXBUtils.getInstance();

	String filename = null;

	public GrammarCompletion(String filename) {
		super();
		this.filename = filename;
	}

	public List<String> toTokens(String str) {
		List<String> tokens = new ArrayList<String>();
		if (str != null && str.length() > 0) {
			String strs[] = str.split(" ");
			if (strs != null) {
				for (String s : strs) {
					tokens.add(s);
				}
			}
		}
		return tokens;
	}

	public Stack<Object> parseGrammar(String sql) throws Exception {

		String str = FileUtil.readFile(filename);
		Grammar grammar =  jaxbUtils.getObject(Grammar.class, str);

		Map<String, Rule> rules = createRuleMap(grammar);

		Stack<Object> expression = new Stack<Object>();
		matchGrammer(grammar, toTokens(sql), rules, expression);

		return expression;
	}

	public Object nextItem(String sql) throws Exception {

		Stack<Object> expression = parseGrammar(sql);

		return nextItem(expression);
	}

	public Object nextItem(Stack<Object> expression) {
		Object item = expression.lastElement();
		Object nextItem = null;
		if (item instanceof Rule) {
			nextItem = item;
		} else if (item instanceof UseRule) {
			// nextItem = rules.get(((UseRule)item).getName());
		} else if (item instanceof Literal || item instanceof Choice || item instanceof Term) {
			Rule rule = null;
			for (int index = expression.size() - 1; index >= 0; index = index - 1) {
				if (expression.get(index) instanceof Rule) {
					rule = (Rule) expression.get(index);
					break;
				}
			}
			int currentIndex = rule.getAny().indexOf(item);
			nextItem = rule.getAny().get(currentIndex + 1);
		}
		return nextItem;
	}

	/**
	 * 
	 */

	private Map<String, Rule> createRuleMap(Grammar grammar) {
		Map<String, Rule> map = new HashMap<String, Rule>();
		for (Rule rule : grammar.getRule()) {
			map.put(rule.getName(), rule);
		}
		return map;
	}

	public ContextMenu createPopupMenu(Object item) {
	    ContextMenu menu = null;
		if (item instanceof Rule) {
			return null;
		} else if (item instanceof UseRule) {
			return null;
		} else if (item instanceof Literal) {
			return null;
		} else if (item instanceof Choice) {
			return null;
		} else if (item instanceof Term) {
			return null;
		}
		return menu;
	}

	/**
	 * 
	 * @param grammar
	 * @param tokens
	 * @param expression
	 * @return
	 */
	private void matchGrammer(Grammar grammar, List<String> tokens, Map<String, Rule> rules, Stack<Object> expression) {
		for (Rule rule : grammar.getRule()) {
			if (matchRule(rule, tokens, rules, expression)) {
				expression.push(rule);
				break;
			}
		}
	}

	/**
	 * 
	 * @param rule
	 * @param tokens
	 * @param expression
	 */
	private boolean matchRule(Rule rule, List<String> tokens, Map<String, Rule> rules, Stack<Object> expression) {
		if (rule == null || tokens == null || tokens.size() == 0) {
			return false;
		}

		List<String> list = tokens;

		Stack<Object> subrule = new Stack<Object>();
		for (Object item : rule.getAny()) {
			if (item instanceof JAXBElement) {
				item = ((JAXBElement) item).getValue();
			}
			if (matchExpression(item, list, rules, subrule)) {
				if (subrule.size() == tokens.size()) {
					break;
				}
				list = tokens.subList(subrule.size(), tokens.size());
			} else if (isRequired(item)) {
				System.out.println("Match Required: " + rule.getName() + ", " + getName(item));
				return false;
			}
		}
		expression.addAll(subrule);
		return true;
	}

	/**
	 * 
	 * @param rule
	 * @param tokens
	 * @param expression
	 * 
	 * @return the index of the tokens which were matched
	 */
	private boolean matchExpression(Object item, List<String> tokens, Map<String, Rule> rules, Stack<Object> expression) {
		if (tokens == null || tokens.size() == 0) {
			return false;
		}
		if (item instanceof Rule) {
			return matchRule((Rule) item, tokens, rules, expression);
		} else if (item instanceof UseRule) {
			String rulename = ((UseRule) item).getName();
			Rule rule = rules.get(((UseRule) item).getName());
			if (rule != null) {
				System.out.println("USeRule found: " + rulename);
			} else {
				System.out.println("USeRule NOT found: " + rulename);
			}
			return matchRule(rule, tokens, rules, expression);
		} else if (item instanceof Literal) {
			return matchLiteral((Literal) item, tokens.get(0), expression);
		} else if (item instanceof Choice) {
			return matchChoice((Choice) item, tokens, rules, expression);
		} else if (item instanceof Term) {
			return matchTerm((Term) item, tokens.get(0), expression);
		}
		return false;
	}

	private boolean matchLiteral(Literal literal, String token, Stack<Object> expression) {
		if (literal == null || token == null) {
			return false;
		}
		if (token.equalsIgnoreCase(literal.getValue())) {
			System.out.println("matchLiteral: " + token + ", " + literal.getValue());
			expression.push(literal);
			return true;
		}
		return false;
	}

	private boolean matchChoice(Choice choice, List<String> tokens, Map<String, Rule> rules, Stack<Object> expression) {
		if (choice == null || tokens == null || tokens.size() == 0) {
			return false;
		}

		for (Object item : choice.getAny()) {
			if (item instanceof JAXBElement) {
				item = ((JAXBElement) item).getValue();
			}
			Stack<Object> subrule = new Stack<Object>();
			if (matchExpression(item, tokens, rules, subrule)) {
				expression.addAll(subrule);
				return true;
			}
		}
		return false;
	}

	private boolean matchTerm(Term term, String token, Stack<Object> expression) {
		if (term == null || token == null) {
			return false;
		}
		expression.push(token);
		return true;
	}

	private boolean isRequired(Object item) {
		if (item instanceof Item) {
			return ((Item) item).isRequired();
		}
		System.out.println("isRequired: unknown handled class: " + item);
		return true;
	}

	private String getName(Object item) {
		if (item instanceof Rule) {
			return ((Rule) item).getName();
		} else if (item instanceof UseRule) {
			return ((UseRule) item).getName();
		} else if (item instanceof Literal) {
			return ((Literal) item).getValue();
		} else if (item instanceof Choice) {
			return ((Choice) item).getName();
		} else if (item instanceof Term) {
			return ((Term) item).getName();
		}
		return item.getClass().getName();
	}
}