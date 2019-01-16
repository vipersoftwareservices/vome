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

package com.viper.vome.converters;

import com.viper.vome.model.FileFormat;

public class ConverterFactory {
    
    public static final ConverterInterface getConverter(FileFormat format) {
        switch (format) {
        case CSV:
           return new ConverterCSV();
        case EXCEL:
            break;
        case HTML:
            break;
        case META:
            break;
        case SQL:
            break;
        case TEXT:
            break;
        case XML:
            break;
        default:
            break;
         
        }
        return null;
    }
     
} 