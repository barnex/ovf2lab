fail=0;

# these files should fail to parse
for f in bad/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		fail=1;
		echo " FAIL: did not return error";
	else
		echo " OK: properly returned error";
	fi;
done;


# these files should parse
for f in parse/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		echo " OK";
	else
		fail=1;
		echo " FAIL";
	fi;
done;

exit $fail;
