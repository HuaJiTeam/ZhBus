package huajiteam.zhbus;

/**
 * Created by KelaKim on 2016/6/8.
 */
public class OpenSourceLicense {
    public String getLicense() {
        String license = "";
        //OKHTTP
        license = license + "OKHTTP\n\n";
        license = license + "Copyright 2016 Square, Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.";

        //GSON
        license = license + "\n\n\n\nGSON\n\n";
        license = license + "Copyright 2008 Google Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.";

        //AOSP
        license = license + "\n\n\n\nAOSP\n\n";
        license = license + "The preferred license for the Android Open Source Project is" +
                " the Apache Software License, Version 2.0 (\"Apache 2.0\"), and the " +
                "majority of the Android software is licensed with Apache 2.0. While the" +
                " project will strive to adhere to the preferred license, there may be " +
                "exceptions that will be handled on a case-by-case basis. For example, " +
                "the Linux kernel patches are under the GPLv2 license with system " +
                "exceptions, which can be found on kernel.org.";
        return license;
    }
}
