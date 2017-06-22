package mtcore;

public enum EAuth {
    Authorized(0),
    AuthRequested(1),
    PairRequested(2),
    Authorizing(3), // pairing
    NotAuthorized(5),
    Unauthorized(6);

    private int value;

    EAuth(int i) {
        value = i;
    }

    public Integer v() { return value; }
}
