module shared.service {
    exports com.mgtechno.shared.json;
    exports com.mgtechno.shared.rest;
    exports com.mgtechno.shared.util;
    exports com.mgtechno.shared.jdbc;
    requires java.base;
    requires java.logging;
    requires java.net.http;
    requires jdk.httpserver;
    requires java.json;
    requires java.sql;
    requires java.sql.rowset;
}
