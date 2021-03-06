#include <boost_fixture.h>
#include <IceUtil/Thread.h>
#include <omero/util/concurrency.h>

using namespace omero::util::concurrency;
using namespace omero;
using namespace std;

class BaseThread : public IceUtil::Thread {
public:
    bool passed;
    IceUtil::Time ms(long ms) {
        return IceUtil::Time::milliSeconds(ms);
    };
};

typedef IceUtil::Handle<BaseThread> BaseThreadPtr;

Event event;

class ReaderThread : public BaseThread {
    virtual void run() {
        if (event.wait(ms(1000))) {
            passed = true;
        }
    }
};

class WriterThread : public BaseThread {
    virtual void run() {
        event.set();
        passed = true;
    }
};

BOOST_AUTO_TEST_CASE( testEvent )
{
        list<BaseThreadPtr> rs;
        list<IceUtil::ThreadControl> rcs;
        for (int i = 0; i < 10; i++) {
            BaseThreadPtr r = new ReaderThread();
            rs.push_back(r);
            rcs.push_back(r->start());
        }

        BaseThreadPtr w = new WriterThread();
        IceUtil::ThreadControl wc = w->start();
        wc.join();
        BOOST_CHECK( (*w).passed );

        for (int i = 0; i < 10; i++) {
            BaseThreadPtr r = rs.front();
            IceUtil::ThreadControl tc = rcs.front();
            rs.pop_front();
            rcs.pop_front();
            tc.join();
            BOOST_CHECK( (*r).passed );
        }
}
