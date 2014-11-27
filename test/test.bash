fail=0;
for f in bad/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		fail=1;
		echo " FAIL: did not return error";
	else
		echo " OK: properly returned error";
	fi;
done;
exit $fail;
