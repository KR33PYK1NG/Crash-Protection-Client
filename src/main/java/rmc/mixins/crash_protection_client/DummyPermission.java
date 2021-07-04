package rmc.mixins.crash_protection_client;

import java.security.Permission;

public class DummyPermission extends Permission {

    @Override
    public boolean implies(Permission permission) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getActions() {
        return this.actions;
    }

    private final String actions;

    public DummyPermission(String name, String actions) {
        super(name);
        this.actions = actions;
    }

}
