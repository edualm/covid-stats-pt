/*
 *  MisconfigurationException.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.exceptions;

public class MisconfigurationException extends Exception {
    public MisconfigurationException(String errorMessage) {
        super(errorMessage);
    }
}
