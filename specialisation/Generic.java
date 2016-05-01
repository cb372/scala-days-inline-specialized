
public class Generic {

    <A> void foo(A a) {
        return;
    }

    void test() {
        foo("hello");
        foo(123);
    }

}

