fail=0;
failed=0;
passed=0;

# these files should fail to parse
for f in bad/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		fail=1;
		(( failed++ ));
		echo " FAIL: did not return error";
	else
		(( passed++ ));
		echo " OK";
	fi;
done;


# these files should parse
for f in parse/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		(( passed++ ));
		echo " OK";
	else
		fail=1;
		(( failed++ ));
		echo " FAIL";
	fi;
done;

echo $failed failed, $passed passed
exit $fail;
