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

package com.viper.vome;

import javafx.scene.control.ContextMenu;

import com.viper.vome.bean.Choice;
import com.viper.vome.bean.Literal;
import com.viper.vome.bean.Rule;
import com.viper.vome.bean.Term;
import com.viper.vome.bean.UseRule;
import com.viper.vome.grammar.GrammarCompletion;

public class EditorSQLSelections {

    GrammarCompletion parser = null;

    public EditorSQLSelections() {
        super();

        parser = new GrammarCompletion("etc/grammar.xml");
    }

    public ContextMenu createPopupMenu(String sql) throws Exception {

        Object item = parser.nextItem(sql);

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

}
