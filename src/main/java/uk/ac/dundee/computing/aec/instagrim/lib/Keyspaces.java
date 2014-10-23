package uk.ac.dundee.computing.aec.instagrim.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createkeyspace = "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreatePicTable = "CREATE TABLE if not exists instagrim.Pics ("
                    + " user varchar,"
                    + " picid uuid, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + "  processedlength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " PRIMARY KEY (picid)"
                    + ")";

            String CreateIndex = "CREATE INDEX ON instagrim.Pics (user)";

            String Createcommentlist = "CREATE TABLE if not exists instagrim.commentlist (\n"
                    + "commentid uuid,\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "comment_added timestamp,\n"
                    + "comment varchar,\n"
                    + "PRIMARY KEY (picid,commentid)\n"
                    + ") WITH CLUSTERING ORDER BY (commentid desc);";

            String Createuserpiclist = "CREATE TABLE if not exists instagrim.userpiclist (\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "pic_added timestamp,\n"
                    + "PRIMARY KEY (user,pic_added)\n"
                    + ") WITH CLUSTERING ORDER BY (pic_added desc);";

            String CreateAddressType = "CREATE TYPE if not exists instagrim.address (\n"
                    + "street text,\n"
                    + "city text,\n"
                    + "post_code text\n"
                    + ");";

            String CreateUserProfile = "CREATE TABLE if not exists instagrim.userprofiles (\n"
                    + "login text PRIMARY KEY,\n"
                    + "password text,\n"
                    + "first_name text,\n"
                    + "last_name text,\n"
                    + "email text,\n"
                    + "friends list<text>,\n"
                    + "addresses map<text, frozen <address>>\n"
                    + ");";

            Session session;
            session = c.connect();
            try {
                PreparedStatement statement = session
                        .prepare(createkeyspace);
                BoundStatement boundStatement = new BoundStatement(
                        statement);
                ResultSet rs = session
                        .execute(boundStatement);
                System.out.println("created instagrim ");
            } catch (Exception et) {
                System.out.println("Can't create instagrim " + et);
            }

            System.out.println("" + CreatePicTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createcommentlist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Commentlist table" + et);
            }
            System.out.println("" + Createcommentlist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreatePicTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create PicTable table " + et);
            }
            System.out.println("" + Createuserpiclist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateIndex);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Index on PicTable table " + et);
            }
            System.out.println("" + CreateIndex);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createuserpiclist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create user pic list table " + et);
            }
            System.out.println("" + CreateAddressType);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateAddressType);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address type " + et);
            }
            System.out.println("" + CreateUserProfile);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserProfile);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address Profile " + et);
            }
            session.close();

        } catch (Exception et) {
            System.out.println("Other keyspace or coulmn definition error" + et);
        }
    }
}
