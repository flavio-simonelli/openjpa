Questo file contiene tutti i passaggi che sono stati effettuati per il progetto:
1. Fork del repository di OpenJPA e creazione di un nuovo branch chiamato "dev", in cui andremo a modificare il progetto per sviluppare i nostri test personali, eliminando tutti i test già esistenti.
2. Rimozione di tutti i test esistenti, rimuovendo direttamente tutte le cartelle "module/src/test".
3. Commentato il plugin maven-antrun-plugin nel pom.xml dei moduli:
    - openjpa-persistence-jdbc
   perché utilizzava uno script di enhancement (enhancer.xml) legato esclusivamente ai test, che sono stati rimossi per esigenze di progetto. La rimozione evita errori di build dovuti all’assenza dei file ".class" generati dai test.
   Inoltre ho tolto openjpa-maven-plugin, che era presente in vari moduli, perché causava errori di build.