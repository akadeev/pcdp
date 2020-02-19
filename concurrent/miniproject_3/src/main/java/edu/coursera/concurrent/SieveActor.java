package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor actor = new SieveActorActor(2);
        finish(() -> {
            for (int i = 3; i <= limit; i++) {
                actor.send(i);
            }
        });
        SieveActorActor lastActor = actor;
        int cnt = 1;
        while (lastActor.getNextActor() != null) {
            cnt++;
            lastActor = lastActor.getNextActor();
        }
        return cnt;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        final private int localPrime;

        private SieveActorActor nextActor;

        public SieveActorActor(int localPrime) {
            this.localPrime = localPrime;
        }

        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            int num = (Integer) msg;
            if (num % localPrime != 0) {
                if (this.nextActor == null) {
                    this.nextActor = new SieveActorActor(num);
                } else {
                    this.nextActor.send(num);
                }
            }
        }

        public SieveActorActor getNextActor() {
            return this.nextActor;
        }
    }
}
