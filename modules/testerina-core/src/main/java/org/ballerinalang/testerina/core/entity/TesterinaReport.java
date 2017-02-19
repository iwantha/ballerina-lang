/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.testerina.core.entity;

import java.io.PrintStream;
import java.util.ArrayList;

public class TesterinaReport {
    private static ArrayList<TesterinaFunctionResult> functionResults = new ArrayList<TesterinaFunctionResult>();
    private static PrintStream outStream = System.err;

    public void addFunctionResult(TesterinaFunctionResult functionResult){
        functionResults.add(functionResult);
    }

    public ArrayList<TesterinaFunctionResult> getFunctionResults() {
        return functionResults;
    }

    private void printTestSummary(){
        int passedFunctionCount = 0 ;
        int failedFunctionCount = 0 ;
        String newLine = System.getProperty("line.separator");
        for (TesterinaFunctionResult result: functionResults) {
            if(result.isTestFunctionPassed()){
                passedFunctionCount ++;
            }
            else{
                failedFunctionCount ++;
            }
        }
        outStream.println("Result : " + newLine + "Test Run : " + functionResults.size() + ", Test Passed: " +
                passedFunctionCount + ", Test Failures: " + failedFunctionCount);
    }
}
