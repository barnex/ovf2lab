fail=0;
for f in bad/*.a2; do
	echo -n ../a2 parse $f;
	(../a2 parse $f 2> /dev/null > /dev/null) && (fail=1; echo " FAIL: did not return error") || echo " OK: properly returned error"
done;
exit $fail
