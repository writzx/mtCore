public enum EAuth {
    Authorized(0), Pending(1), NotAuthorized(2), Unauthorized(3);

    private int value;

    EAuth(int i) {
        value = i;
    }

    public Integer v() { return value; }
}
