/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.cli;

import java.util.List;
import org.apache.commons.cli.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

/**
 * create command for cli
 */
public class CreateCommand extends CliCommand {

    private static Options options = new Options();
    private String[] args;
    private CommandLine cl;
    
    {
        options.addOption(new Option("e", false, "ephemeral"));
        options.addOption(new Option("s", false, "sequential"));
    }
    
    public CreateCommand() {
        super("create", "[-s] [-e] path [data] [acl]");
    }
    

    @Override
    public CliCommand parse(String[] cmdArgs) throws ParseException {
        Parser parser = new PosixParser();
        cl = parser.parse(options, cmdArgs);
        args = cl.getArgs();
        if(args.length < 2) {
            throw new ParseException(getUsageStr());
        }
        return this;
    }

    
    @Override
    public boolean exec() throws KeeperException, InterruptedException {
        CreateMode flags = CreateMode.PERSISTENT;
        if(cl.hasOption("e") && cl.hasOption("s")) {
            flags = CreateMode.EPHEMERAL_SEQUENTIAL;
        } else if (cl.hasOption("e")) {
            flags = CreateMode.EPHEMERAL;
        } else if (cl.hasOption("s")) {
            flags = CreateMode.PERSISTENT_SEQUENTIAL;
        }
        String path = args[1];
        byte[] data = null;
        if (args.length > 2) {
            data = args[2].getBytes();
        }
        List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        if (args.length > 3) {
            acl = AclParser.parse(args[3]);
        }
        String newPath = zk.create(path, data, acl, flags);
        err.println("Created " + newPath);
        return true;
    }
}
