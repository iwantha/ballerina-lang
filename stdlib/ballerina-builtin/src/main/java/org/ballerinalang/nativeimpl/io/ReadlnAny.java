/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.ballerinalang.nativeimpl.io;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Native function ballerina/io:readln.
 *
 * @since 0.97
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "io",
        functionName = "readln",
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true
)
public class ReadlnAny extends BlockingNativeCallableUnit {

    public void execute(Context ctx) {
        BValue result = ctx.getNullableRefArgument(0);
        if (result != null) {
            System.out.print(result.stringValue());
        }
        Scanner sc = new Scanner(System.in, Charset.defaultCharset().displayName());
        String input = sc.nextLine();
        ctx.setReturnValues(new BString(input));
    }
}
