public enum EAuth {
    Authorized(0), AuthRequested(1), PairRequested(2), RequestingPair(3), NotAuthorized(5), Unauthorized(6);

    private int value;

    EAuth(int i) {
        value = i;
    }

    public Integer v() { return value; }
}
