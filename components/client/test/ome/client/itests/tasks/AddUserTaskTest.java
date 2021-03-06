/*
 * ome.client.itests.tasks.AddUserTaskTest
 *
 *   Copyright 2006 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.client.itests.tasks;

import org.testng.annotations.*;

import java.util.Properties;
import java.util.UUID;

import ome.util.tasks.Run;
import ome.util.tasks.admin.AddUserTask;

public class AddUserTaskTest extends AbstractAdminTaskTest {

    @Test
    public void testSimple() throws Exception {
        String group = makeGroup();
        Properties p = new Properties();
        p.setProperty("omename", UUID.randomUUID().toString());
        p.setProperty("firstname", "task");
        p.setProperty("lastname", "test");
        p.setProperty("group", group);
        new AddUserTask(root, p).run();
    }

    @Test
    public void testViaCommandLine() throws Exception {
        String group = makeGroup();
        Run.main(join(rootString, new String[] { "task=admin.AddUserTask",
                "firstname=task", "lastname=test", "group=" + group,
                "omename=" + UUID.randomUUID().toString() }));
    }

}
